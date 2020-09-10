package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.BonusHistory;
import com.roundrobine.movie.rentals.repository.BonusHistoryRepository;
import com.roundrobine.movie.rentals.repository.search.BonusHistorySearchRepository;
import com.roundrobine.movie.rentals.service.dto.BonusHistoryDTO;
import com.roundrobine.movie.rentals.service.mapper.BonusHistoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link BonusHistory}.
 */
@Service
@Transactional
public class BonusHistoryService {

    private final Logger log = LoggerFactory.getLogger(BonusHistoryService.class);

    private final BonusHistoryRepository bonusHistoryRepository;

    private final BonusHistoryMapper bonusHistoryMapper;

    private final BonusHistorySearchRepository bonusHistorySearchRepository;

    public BonusHistoryService(BonusHistoryRepository bonusHistoryRepository, BonusHistoryMapper bonusHistoryMapper, BonusHistorySearchRepository bonusHistorySearchRepository) {
        this.bonusHistoryRepository = bonusHistoryRepository;
        this.bonusHistoryMapper = bonusHistoryMapper;
        this.bonusHistorySearchRepository = bonusHistorySearchRepository;
    }

    /**
     * Save a bonusHistory.
     *
     * @param bonusHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    public BonusHistoryDTO save(BonusHistoryDTO bonusHistoryDTO) {
        log.debug("Request to save BonusHistory : {}", bonusHistoryDTO);
        BonusHistory bonusHistory = bonusHistoryMapper.toEntity(bonusHistoryDTO);
        bonusHistory = bonusHistoryRepository.save(bonusHistory);
        BonusHistoryDTO result = bonusHistoryMapper.toDto(bonusHistory);
        bonusHistorySearchRepository.save(bonusHistory);
        return result;
    }


    /**
     * Save a bonusHistory.
     *
     * @param bonusHistory the entity to save.
     * @return the persisted entity.
     */
    public BonusHistory save(BonusHistory bonusHistory) {
        log.debug("Request to save BonusHistory : {}", bonusHistory);
        bonusHistory = bonusHistoryRepository.save(bonusHistory);
        bonusHistorySearchRepository.save(bonusHistory);
        return bonusHistory;
    }

    /**
     * Get all the bonusHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BonusHistoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BonusHistories");
        return bonusHistoryRepository.findAll(pageable)
            .map(bonusHistoryMapper::toDto);
    }


    /**
     * Get one bonusHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BonusHistoryDTO> findOne(Long id) {
        log.debug("Request to get BonusHistory : {}", id);
        return bonusHistoryRepository.findById(id)
            .map(bonusHistoryMapper::toDto);
    }

    /**
     * Delete the bonusHistory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BonusHistory : {}", id);
        bonusHistoryRepository.deleteById(id);
        bonusHistorySearchRepository.deleteById(id);
    }

    /**
     * Search for the bonusHistory corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BonusHistoryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BonusHistories for query {}", query);
        return bonusHistorySearchRepository.search(queryStringQuery(query), pageable)
            .map(bonusHistoryMapper::toDto);
    }
}
