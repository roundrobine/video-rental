package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.service.RentedCopyService;
import com.roundrobine.movie.rentals.web.rest.errors.BadRequestAlertException;
import com.roundrobine.movie.rentals.service.dto.RentedCopyDTO;

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
 * REST controller for managing {@link com.roundrobine.movie.rentals.domain.RentedCopy}.
 */
@RestController
@RequestMapping("/api")
public class RentedCopyResource {

    private final Logger log = LoggerFactory.getLogger(RentedCopyResource.class);

    private static final String ENTITY_NAME = "rentedCopy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentedCopyService rentedCopyService;

    public RentedCopyResource(RentedCopyService rentedCopyService) {
        this.rentedCopyService = rentedCopyService;
    }

    /**
     * {@code POST  /rented-copies} : Create a new rentedCopy.
     *
     * @param rentedCopyDTO the rentedCopyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rentedCopyDTO, or with status {@code 400 (Bad Request)} if the rentedCopy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rented-copies")
    public ResponseEntity<RentedCopyDTO> createRentedCopy(@Valid @RequestBody RentedCopyDTO rentedCopyDTO) throws URISyntaxException {
        log.debug("REST request to save RentedCopy : {}", rentedCopyDTO);
        if (rentedCopyDTO.getId() != null) {
            throw new BadRequestAlertException("A new rentedCopy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RentedCopyDTO result = rentedCopyService.save(rentedCopyDTO);
        return ResponseEntity.created(new URI("/api/rented-copies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rented-copies} : Updates an existing rentedCopy.
     *
     * @param rentedCopyDTO the rentedCopyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentedCopyDTO,
     * or with status {@code 400 (Bad Request)} if the rentedCopyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rentedCopyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rented-copies")
    public ResponseEntity<RentedCopyDTO> updateRentedCopy(@Valid @RequestBody RentedCopyDTO rentedCopyDTO) throws URISyntaxException {
        log.debug("REST request to update RentedCopy : {}", rentedCopyDTO);
        if (rentedCopyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RentedCopyDTO result = rentedCopyService.save(rentedCopyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rentedCopyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /rented-copies} : get all the rentedCopies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentedCopies in body.
     */
    @GetMapping("/rented-copies")
    public ResponseEntity<List<RentedCopyDTO>> getAllRentedCopies(Pageable pageable) {
        log.debug("REST request to get a page of RentedCopies");
        Page<RentedCopyDTO> page = rentedCopyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rented-copies/:id} : get the "id" rentedCopy.
     *
     * @param id the id of the rentedCopyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentedCopyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rented-copies/{id}")
    public ResponseEntity<RentedCopyDTO> getRentedCopy(@PathVariable Long id) {
        log.debug("REST request to get RentedCopy : {}", id);
        Optional<RentedCopyDTO> rentedCopyDTO = rentedCopyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rentedCopyDTO);
    }

    /**
     * {@code DELETE  /rented-copies/:id} : delete the "id" rentedCopy.
     *
     * @param id the id of the rentedCopyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rented-copies/{id}")
    public ResponseEntity<Void> deleteRentedCopy(@PathVariable Long id) {
        log.debug("REST request to delete RentedCopy : {}", id);
        rentedCopyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/rented-copies?query=:query} : search for the rentedCopy corresponding
     * to the query.
     *
     * @param query the query of the rentedCopy search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/rented-copies")
    public ResponseEntity<List<RentedCopyDTO>> searchRentedCopies(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of RentedCopies for query {}", query);
        Page<RentedCopyDTO> page = rentedCopyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
