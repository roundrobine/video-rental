package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.MovieInventory;
import com.roundrobine.movie.rentals.repository.MovieInventoryRepository;
import com.roundrobine.movie.rentals.repository.search.MovieInventorySearchRepository;
import com.roundrobine.movie.rentals.service.dto.MovieInventoryDTO;
import com.roundrobine.movie.rentals.service.mapper.MovieInventoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link MovieInventory}.
 */
@Service
@Transactional
public class MovieInventoryService {

    private final Logger log = LoggerFactory.getLogger(MovieInventoryService.class);

    private final MovieInventoryRepository movieInventoryRepository;

    private final MovieInventoryMapper movieInventoryMapper;

    private final MovieInventorySearchRepository movieInventorySearchRepository;

    public MovieInventoryService(MovieInventoryRepository movieInventoryRepository, MovieInventoryMapper movieInventoryMapper, MovieInventorySearchRepository movieInventorySearchRepository) {
        this.movieInventoryRepository = movieInventoryRepository;
        this.movieInventoryMapper = movieInventoryMapper;
        this.movieInventorySearchRepository = movieInventorySearchRepository;
    }

    /**
     * Save a movieInventory.
     *
     * @param movieInventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public MovieInventoryDTO save(MovieInventoryDTO movieInventoryDTO) {
        log.debug("Request to save MovieInventory : {}", movieInventoryDTO);
        movieInventoryDTO.setLastUpdatedAt(Instant.now());
        MovieInventory movieInventory = movieInventoryMapper.toEntity(movieInventoryDTO);
        movieInventory = movieInventoryRepository.save(movieInventory);
        MovieInventoryDTO result = movieInventoryMapper.toDto(movieInventory);
        movieInventorySearchRepository.save(movieInventory);
        return result;
    }


    /**
     * Save a list of movieInventory.
     *
     * @param movieInventoryList the entity to save.
     * @return the persisted entity.
     */
    public boolean saveAll(List<MovieInventory> movieInventoryList) {
        log.debug("Request to save a list of MovieInventory : {}", movieInventoryList);
        movieInventoryRepository.saveAll(movieInventoryList);
        movieInventorySearchRepository.saveAll(movieInventoryList);
        return true;
    }

    /**
     * Get all the movieInventories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MovieInventoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MovieInventories");
        return movieInventoryRepository.findAll(pageable)
            .map(movieInventoryMapper::toDto);
    }


    /**
     * Get one movieInventory DTO by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MovieInventoryDTO> findOne(Long id) {
        log.debug("Request to get MovieInventory : {}", id);
        return movieInventoryRepository.findById(id)
            .map(movieInventoryMapper::toDto);
    }


    /**
     * Get one movieInventory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MovieInventory> findOneInternal(Long id) {
        log.debug("Request to get MovieInventory : {}", id);
        return movieInventoryRepository.findById(id);
    }


    /**
     * Get all movieInventories by list of matching ids.
     *
     * @param  ids  the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MovieInventory> findByIdIn(Set<Long> ids) {
        log.debug("Request to get all MovieInventories by passing a list of matching ids");
        return movieInventoryRepository.findByIdIn(ids);
    }

    /**
     * Delete the movieInventory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete MovieInventory : {}", id);
        movieInventoryRepository.deleteById(id);
        movieInventorySearchRepository.deleteById(id);
    }

    /**
     * Search for the movieInventory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MovieInventoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of MovieInventories for query {}", query);
        return movieInventorySearchRepository.search(queryStringQuery(query), pageable)
            .map(movieInventoryMapper::toDto);
    }




    /**
     * Read all media entities and refresh the data in elastic search
     */
    @Scheduled(fixedDelay = 500000)
    public void refreshSearchRepo() {
        this.movieInventoryRepository.findAll()
            .forEach(m -> this.movieInventorySearchRepository.save(m));
    }
}
