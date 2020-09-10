package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.*;
import com.roundrobine.movie.rentals.domain.enumeration.Currency;
import com.roundrobine.movie.rentals.domain.enumeration.OrderStatus;
import com.roundrobine.movie.rentals.domain.enumeration.RentalStatus;
import com.roundrobine.movie.rentals.repository.RentalOrderRepository;
import com.roundrobine.movie.rentals.repository.search.RentalOrderSearchRepository;
import com.roundrobine.movie.rentals.service.dto.CreateRentalOrderDTO;
import com.roundrobine.movie.rentals.service.dto.RentalOrderDTO;
import com.roundrobine.movie.rentals.service.dto.ReturnRentedMovieDTO;
import com.roundrobine.movie.rentals.service.mapper.RentalOrderMapper;
import com.roundrobine.movie.rentals.web.rest.errors.BadRequestAlertException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
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

    public static final String ORDER_RENTAL_SERVICE = "OrderRentalService";


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

            log.debug("Start processing new rental order {}", createRentalOrderDTO);

             List<MovieInventory> moviesToRent = validateAndGetMovieInventoryList(createRentalOrderDTO);
             List<MovieInventory> rentedMovies = new ArrayList<>();

             Customer customer = lookForCustomer(user);

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
                    "Please deposit some money and try again.", ORDER_RENTAL_SERVICE, "customernotvalid");
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


    @Transactional
    public List<RentalOrderDTO> returnRentedMovieCopies(User user, ReturnRentedMovieDTO returnRentedMoviesDTO) {

        List<MovieInventory> moviesToReturn = validateAndGetMoviesToReturn(returnRentedMoviesDTO);

        Customer customer = lookForCustomer(user);

        List<RentedCopy> copiesToReturn = rentedCopyService
            .findByMovieInventoryIdIn(new HashSet<>(returnRentedMoviesDTO.getMovieInventoryIds()));

        validateReturnedMoviesWithCustomer(customer, copiesToReturn);

        copiesToReturn = copiesToReturn.stream()
            .filter(copy -> copy.getReturnDate() == null)
            .collect(Collectors.toList());

        if(copiesToReturn.isEmpty()){
            throw new BadRequestAlertException("No movies currently rented by this customer!",
                ORDER_RENTAL_SERVICE, "customernotvalid");
        }

        Set<Long> rentalOrderIds =
            copiesToReturn.stream().map(copy -> copy.getOrder().getId()).collect(Collectors.toSet());

        List<RentalOrder> rentalOrders = rentalOrderRepository.findByIdIn(rentalOrderIds);

        // Customer may return movies that belong to different orders at once so we need to
        // accumulate the total surcharges to pay across all orders;
        BigDecimal totalSurchargesAmountToPay = new BigDecimal("0") ;


        for(RentedCopy copy:copiesToReturn) {
            copy.setReturnDate(Instant.now());
            long actualRentDurationInDays = Duration.between(copy.getRentDate(), copy.getReturnDate()).toDays();

            BigDecimal surcharges = copy.getMovieInventory().getMovie().getType()
                .calculateSurchargesOnMovieReturn(copy.getPlannedRentDuration(), (int) actualRentDurationInDays);
            copy.setExtraChargedDays(surcharges
                .divide(copy.getMovieInventory().getMovie().getType().getPrice()).intValue());

            rentalOrders = rentalOrders.stream()
                .map(rentalOrder -> {
                    if(rentalOrder.getId() == copy.getOrder().getId()){
                        rentalOrder.setLateChargedAmount(copy.getOrder().getLateChargedAmount().add(surcharges));
                        rentalOrder.setTotalAmount(copy.getOrder().getTotalAmount()
                            .add(surcharges));
                        rentalOrder.setLastUpdatedAt(Instant.now());
                    }
                    return rentalOrder;
                }).collect(Collectors.toList());


            totalSurchargesAmountToPay = totalSurchargesAmountToPay.add(surcharges);

        }


        if(!customerService.isCustomerAbleToPay(customer, totalSurchargesAmountToPay) ){
            throw new BadRequestAlertException("Order will not be created because customer " +
                "does not have a sufficient balance. " +
                "Please deposit some money and try again.",  ORDER_RENTAL_SERVICE, "customernotvalid");
        }else {
            log.info("Customer balance will be deduced for {} SEK in total", totalSurchargesAmountToPay);
            customer.setCreditAmount(customer.getCreditAmount().subtract(totalSurchargesAmountToPay));
        }


        moviesToReturn = moviesToReturn.stream().map(movieInventory -> {
            movieInventory.setStatus(RentalStatus.AVAILABLE);
            movieInventory.setLastUpdatedAt(Instant.now());
            return movieInventory;
        }).collect(Collectors.toList());


        movieInventoryService.saveAll(moviesToReturn);
        log.info("Movies are updated to be available for rent {}", moviesToReturn);
        rentedCopyService.saveAll(copiesToReturn);
        log.info("Customer rental history has bean updated {}", copiesToReturn);

        List<RentalOrder> updatedRentalOrderList = rentalOrderRepository.saveAll(new ArrayList<>(rentalOrders));
        rentalOrderSearchRepository.saveAll(updatedRentalOrderList);


        //we will need to get all related rented movie copies to the specific order to check if they are all returned
        // so that we could update order status to completed

        updatedRentalOrderList = updatedRentalOrderList.stream().map(order -> {
            if(order.getRentedCopies().stream().allMatch(copy -> copy.getReturnDate() != null)){
                order.setStatus(OrderStatus.COMPLETED);
                order = rentalOrderRepository.save(order);
                rentalOrderSearchRepository.save(order);
            }
            return order;
        }).collect(Collectors.toList());

        log.info("Movie rental copies has been returned and all orders and customer {} balance has been updated", customer);

        return rentalOrderMapper.toDto(updatedRentalOrderList);

    }



    private List<MovieInventory> validateAndGetMovieInventoryList(CreateRentalOrderDTO createRentalOrderDTO) {

        Set<Long> inventoryIds = createRentalOrderDTO.getOrder().keySet();
        List<MovieInventory> moviesToRent = movieInventoryService.findByIdIn(inventoryIds);

        moviesToRent = moviesToRent.stream()
            .filter(m -> m.getStatus() == RentalStatus.AVAILABLE)
            .collect(Collectors.toList());

        if(inventoryIds.size() != moviesToRent.size()){
            throw new BadRequestAlertException("Some of the provided movie ids {} are not valid or already rented, " +
                "the order will be canceled!",ORDER_RENTAL_SERVICE, "idsnotvalid");
        }

        return moviesToRent;
    }


    private List<MovieInventory> validateAndGetMoviesToReturn(ReturnRentedMovieDTO returnRentedMoviesDTO) {

        Set<Long> inventoryIds = new HashSet<>(returnRentedMoviesDTO.getMovieInventoryIds());
        List<MovieInventory> moviesToReturn = movieInventoryService.findByIdIn(inventoryIds);

        moviesToReturn = moviesToReturn.stream()
            .filter(m -> m.getStatus() == RentalStatus.RENTED)
            .collect(Collectors.toList());

        if(inventoryIds.size() != moviesToReturn.size()){
            throw new BadRequestAlertException("Some of the provided movie ids {} are not valid or available for rent, " +
                "the return movie action will be canceled!",ORDER_RENTAL_SERVICE, "idsnotvalid");
        }

        return moviesToReturn;
    }


    private Customer lookForCustomer(User user){
        log.debug("Look for the customer attached to specific user");
        Optional<Customer> customerOpt = customerService.findOneInternal(user.getId());
        if(!customerOpt.isPresent()){
            throw new BadRequestAlertException("Customer with this id does not exists!",
                ORDER_RENTAL_SERVICE, "customernotvalid");
        }
        return customerOpt.get();

    }


    private boolean validateReturnedMoviesWithCustomer(Customer customer, List<RentedCopy> copies){

            if(!copies.stream().allMatch(copy -> copy.getOrder().getCustomer().getId() == customer.getId()))   {
                throw new BadRequestAlertException("Some of the returned movies does not belong to the specific customer",
                    ORDER_RENTAL_SERVICE, "customernotvalid");
            }

            return true;
    }

}
