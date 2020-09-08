package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.service.BonusHistoryService;
import com.roundrobine.movie.rentals.web.rest.errors.BadRequestAlertException;
import com.roundrobine.movie.rentals.service.dto.BonusHistoryDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.roundrobine.movie.rentals.domain.BonusHistory}.
 */
@RestController
@RequestMapping("/api")
public class BonusHistoryResource {

    private final Logger log = LoggerFactory.getLogger(BonusHistoryResource.class);

    private static final String ENTITY_NAME = "bonusHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BonusHistoryService bonusHistoryService;

    public BonusHistoryResource(BonusHistoryService bonusHistoryService) {
        this.bonusHistoryService = bonusHistoryService;
    }

    /**
     * {@code POST  /bonus-histories} : Create a new bonusHistory.
     *
     * @param bonusHistoryDTO the bonusHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bonusHistoryDTO, or with status {@code 400 (Bad Request)} if the bonusHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bonus-histories")
    public ResponseEntity<BonusHistoryDTO> createBonusHistory(@Valid @RequestBody BonusHistoryDTO bonusHistoryDTO) throws URISyntaxException {
        log.debug("REST request to save BonusHistory : {}", bonusHistoryDTO);
        if (bonusHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new bonusHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BonusHistoryDTO result = bonusHistoryService.save(bonusHistoryDTO);
        return ResponseEntity.created(new URI("/api/bonus-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bonus-histories} : Updates an existing bonusHistory.
     *
     * @param bonusHistoryDTO the bonusHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bonusHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the bonusHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bonusHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bonus-histories")
    public ResponseEntity<BonusHistoryDTO> updateBonusHistory(@Valid @RequestBody BonusHistoryDTO bonusHistoryDTO) throws URISyntaxException {
        log.debug("REST request to update BonusHistory : {}", bonusHistoryDTO);
        if (bonusHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BonusHistoryDTO result = bonusHistoryService.save(bonusHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, bonusHistoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /bonus-histories} : get all the bonusHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bonusHistories in body.
     */
    @GetMapping("/bonus-histories")
    public ResponseEntity<List<BonusHistoryDTO>> getAllBonusHistories(Pageable pageable) {
        log.debug("REST request to get a page of BonusHistories");
        Page<BonusHistoryDTO> page = bonusHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bonus-histories/:id} : get the "id" bonusHistory.
     *
     * @param id the id of the bonusHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bonusHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bonus-histories/{id}")
    public ResponseEntity<BonusHistoryDTO> getBonusHistory(@PathVariable Long id) {
        log.debug("REST request to get BonusHistory : {}", id);
        Optional<BonusHistoryDTO> bonusHistoryDTO = bonusHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bonusHistoryDTO);
    }

    /**
     * {@code DELETE  /bonus-histories/:id} : delete the "id" bonusHistory.
     *
     * @param id the id of the bonusHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bonus-histories/{id}")
    public ResponseEntity<Void> deleteBonusHistory(@PathVariable Long id) {
        log.debug("REST request to delete BonusHistory : {}", id);
        bonusHistoryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/bonus-histories?query=:query} : search for the bonusHistory corresponding
     * to the query.
     *
     * @param query the query of the bonusHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/bonus-histories")
    public ResponseEntity<List<BonusHistoryDTO>> searchBonusHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of BonusHistories for query {}", query);
        Page<BonusHistoryDTO> page = bonusHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
