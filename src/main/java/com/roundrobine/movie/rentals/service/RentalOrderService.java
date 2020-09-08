package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.RentalOrder;
import com.roundrobine.movie.rentals.repository.RentalOrderRepository;
import com.roundrobine.movie.rentals.repository.search.RentalOrderSearchRepository;
import com.roundrobine.movie.rentals.service.dto.RentalOrderDTO;
import com.roundrobine.movie.rentals.service.mapper.RentalOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link RentalOrder}.
 */
@Service
@Transactional
public class RentalOrderService {

    private final Logger log = LoggerFactory.getLogger(RentalOrderService.class);

    private final RentalOrderRepository rentalOrderRepository;

    private final RentalOrderMapper rentalOrderMapper;

    private final RentalOrderSearchRepository rentalOrderSearchRepository;

    public RentalOrderService(RentalOrderRepository rentalOrderRepository, RentalOrderMapper rentalOrderMapper, RentalOrderSearchRepository rentalOrderSearchRepository) {
        this.rentalOrderRepository = rentalOrderRepository;
        this.rentalOrderMapper = rentalOrderMapper;
        this.rentalOrderSearchRepository = rentalOrderSearchRepository;
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
}
