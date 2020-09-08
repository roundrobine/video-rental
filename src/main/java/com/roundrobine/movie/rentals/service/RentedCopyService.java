package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.RentedCopy;
import com.roundrobine.movie.rentals.repository.RentedCopyRepository;
import com.roundrobine.movie.rentals.repository.search.RentedCopySearchRepository;
import com.roundrobine.movie.rentals.service.dto.RentedCopyDTO;
import com.roundrobine.movie.rentals.service.mapper.RentedCopyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link RentedCopy}.
 */
@Service
@Transactional
public class RentedCopyService {

    private final Logger log = LoggerFactory.getLogger(RentedCopyService.class);

    private final RentedCopyRepository rentedCopyRepository;

    private final RentedCopyMapper rentedCopyMapper;

    private final RentedCopySearchRepository rentedCopySearchRepository;

    public RentedCopyService(RentedCopyRepository rentedCopyRepository, RentedCopyMapper rentedCopyMapper, RentedCopySearchRepository rentedCopySearchRepository) {
        this.rentedCopyRepository = rentedCopyRepository;
        this.rentedCopyMapper = rentedCopyMapper;
        this.rentedCopySearchRepository = rentedCopySearchRepository;
    }

    /**
     * Save a rentedCopy.
     *
     * @param rentedCopyDTO the entity to save.
     * @return the persisted entity.
     */
    public RentedCopyDTO save(RentedCopyDTO rentedCopyDTO) {
        log.debug("Request to save RentedCopy : {}", rentedCopyDTO);
        RentedCopy rentedCopy = rentedCopyMapper.toEntity(rentedCopyDTO);
        rentedCopy = rentedCopyRepository.save(rentedCopy);
        RentedCopyDTO result = rentedCopyMapper.toDto(rentedCopy);
        rentedCopySearchRepository.save(rentedCopy);
        return result;
    }

    /**
     * Get all the rentedCopies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RentedCopyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RentedCopies");
        return rentedCopyRepository.findAll(pageable)
            .map(rentedCopyMapper::toDto);
    }


    /**
     * Get one rentedCopy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RentedCopyDTO> findOne(Long id) {
        log.debug("Request to get RentedCopy : {}", id);
        return rentedCopyRepository.findById(id)
            .map(rentedCopyMapper::toDto);
    }

    /**
     * Delete the rentedCopy by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RentedCopy : {}", id);
        rentedCopyRepository.deleteById(id);
        rentedCopySearchRepository.deleteById(id);
    }

    /**
     * Search for the rentedCopy corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RentedCopyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RentedCopies for query {}", query);
        return rentedCopySearchRepository.search(queryStringQuery(query), pageable)
            .map(rentedCopyMapper::toDto);
    }
}
