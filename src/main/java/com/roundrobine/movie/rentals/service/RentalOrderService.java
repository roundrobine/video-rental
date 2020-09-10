package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.*;
import com.roundrobine.movie.rentals.domain.enumeration.Currency;
import com.roundrobine.movie.rentals.domain.enumeration.OrderStatus;
import com.roundrobine.movie.rentals.domain.enumeration.RentalStatus;
import com.roundrobine.movie.rentals.repository.RentalOrderRepository;
import com.roundrobine.movie.rentals.repository.search.RentalOrderSearchRepository;
import com.roundrobine.movie.rentals.service.dto.CreateRentalOrderDTO;
import com.roundrobine.movie.rentals.service.dto.RentalOrderDTO;
import com.roundrobine.movie.rentals.service.mapper.RentalOrderMapper;
import com.roundrobine.movie.rentals.web.rest.errors.BadRequestAlertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link RentalOrder}.
 */
@Service
@Transactional
@Slf4j
public class RentalOrderService {

    private final RentalOrderRepository rentalOrderRepository;

    private final RentalOrderMapper rentalOrderMapper;

    private final RentalOrderSearchRepository rentalOrderSearchRepository;

    private final MovieInventoryService movieInventoryService;

    private final CustomerService customerService;

    private final RentedCopyService rentedCopyService;

    private final BonusHistoryService bonusHistoryService;


    public RentalOrderService(RentalOrderRepository rentalOrderRepository, RentalOrderMapper rentalOrderMapper,
                              RentalOrderSearchRepository rentalOrderSearchRepository,
                              MovieInventoryService movieInventoryService, CustomerService customerService,
                              RentedCopyService rentedCopyService, BonusHistoryService bonusHistoryService) {
        this.rentalOrderRepository = rentalOrderRepository;
        this.rentalOrderMapper = rentalOrderMapper;
        this.rentalOrderSearchRepository = rentalOrderSearchRepository;
        this.movieInventoryService = movieInventoryService;
        this.customerService = customerService;
        this.rentedCopyService = rentedCopyService;
        this.bonusHistoryService = bonusHistoryService;
    }

    /**
     * Save a rentalOrder.
     *
     * @param rentalOrderDTO the entity to save.
     * @return the persisted entity.
     */
    public RentalOrderDTO save(RentalOrderDTO rentalOrderDTO) {
        log.debug("Request to save RentalOrder : {}", rentalOrderDTO);
        RentalOrder rentalOrder = rentalOrderMapper.toEntity(rentalOrderDTO);
        rentalOrder = rentalOrderRepository.save(rentalOrder);
        RentalOrderDTO result = rentalOrderMapper.toDto(rentalOrder);
        rentalOrderSearchRepository.save(rentalOrder);
        return result;
    }

    /**
     * Get all the rentalOrders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RentalOrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RentalOrders");
        return rentalOrderRepository.findAll(pageable)
            .map(rentalOrderMapper::toDto);
    }


    /**
     * Get one rentalOrder by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RentalOrderDTO> findOne(Long id) {
        log.debug("Request to get RentalOrder : {}", id);
        return rentalOrderRepository.findById(id)
            .map(rentalOrderMapper::toDto);
    }

    /**
     * Delete the rentalOrder by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RentalOrder : {}", id);
        rentalOrderRepository.deleteById(id);
        rentalOrderSearchRepository.deleteById(id);
    }

    /**
     * Search for the rentalOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RentalOrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RentalOrders for query {}", query);
        return rentalOrderSearchRepository.search(queryStringQuery(query), pageable)
            .map(rentalOrderMapper::toDto);
    }


    @Transactional
    public RentalOrderDTO processRentalOrder(User user, CreateRentalOrderDTO createRentalOrderDTO){

             List<MovieInventory> moviesToRent = validateAndGetMovieInventoryList(createRentalOrderDTO);
             List<MovieInventory> rentedMovies = new ArrayList<>();

            Optional<Customer> customerOpt = customerService.findOneInternal(user.getId());
            Customer customer;
            if(!customerOpt.isPresent()){
                throw new BadRequestAlertException("Customer with this id does not exists!",
                    "OrderRentalService", "customernotvalid");
            }
            customer = customerOpt.get();

            // Create a new order
            RentalOrder order = rentalOrderRepository.save(RentalOrder.builder()
                 .currency(Currency.SEK)
                 .customer(customer)
                 .build());

            rentalOrderSearchRepository.save(order);
            BonusHistory bonusHistory = BonusHistory.builder()
            .customer(customer)
            .oder(order)
            .build();

            List<RentedCopy> movieCopies = new ArrayList<>();

            createRentalOrderDTO.getOrder().forEach(
                    (movieInventoryId, plannedRentDuration) -> {

                        MovieInventory movieInventory = moviesToRent.stream()
                                .filter(mi -> mi.getId() == movieInventoryId).collect(Collectors.toList()).get(0);

                        movieCopies.add(
                            RentedCopy.builder()
                                .movieInventory(movieInventory)
                                .plannedRentDuration(plannedRentDuration)
                                .order(order)
                                .build());

                        movieInventory.setStatus(RentalStatus.RENTED);
                        movieInventory.setLastUpdatedAt(Instant.now());
                        rentedMovies.add(movieInventory);

                        order.setTotalAmount(order.getTotalAmount()
                            .add(movieInventory.getMovie().getType().calculateMoviePriceOnRental(plannedRentDuration)));
                        bonusHistory
                            .setPoints(bonusHistory.getPoints() + movieInventory.getMovie().getType().getBonusPoints());
                    });


            if(!customerService.isCustomerAbleToPay(customer, order.getTotalAmount()) ){
                throw new BadRequestAlertException("Order will not be created because customer " +
                    "does not have a sufficient balance. " +
                    "Please deposit some money and try again.",  "OrderRentalService", "customernotvalid");
            }else {
                customer.setCreditAmount(customer.getCreditAmount().subtract(order.getTotalAmount()));
                customer.setBonusPoints(customer.getBonusPoints() + bonusHistory.getPoints());
            }

            movieInventoryService.saveAll(rentedMovies);
            rentedCopyService.saveAll(movieCopies);
            bonusHistoryService.save(bonusHistory);

            //update order with final data
            order.setRentedCopies(new HashSet<>(movieCopies));
            order.setStatus(OrderStatus.ACTIVE);
            order.setLastUpdatedAt(Instant.now());
            rentalOrderRepository.save(order);
            rentalOrderSearchRepository.save(order);

            log.info("New order {} for customer {} has been successfully created!", order.getId(), customer);

        return rentalOrderMapper.toDto(order);

    }


    private List<MovieInventory> validateAndGetMovieInventoryList(CreateRentalOrderDTO createRentalOrderDTO) {

        Set<Long> inventoryIds = createRentalOrderDTO.getOrder().keySet();
        List<MovieInventory> moviesToRent = movieInventoryService.findByIdIn(inventoryIds);

        moviesToRent = moviesToRent.stream()
            .filter(m -> m.getStatus() == RentalStatus.AVAILABLE)
            .collect(Collectors.toList());

        if(inventoryIds.size() != moviesToRent.size()){
            throw new BadRequestAlertException("Some of the provided movie ids {} are not valid or already rented, " +
                "the order will be canceled!", "OrderRentalService", "idsnotvalid");
        }

        return moviesToRent;
    }


}
