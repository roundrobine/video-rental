package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.service.MovieInventoryService;
import com.roundrobine.movie.rentals.web.rest.errors.BadRequestAlertException;
import com.roundrobine.movie.rentals.service.dto.MovieInventoryDTO;

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
 * REST controller for managing {@link com.roundrobine.movie.rentals.domain.MovieInventory}.
 */
@RestController
@RequestMapping("/api")
public class MovieInventoryResource {

    private final Logger log = LoggerFactory.getLogger(MovieInventoryResource.class);

    private static final String ENTITY_NAME = "movieInventory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MovieInventoryService movieInventoryService;

    public MovieInventoryResource(MovieInventoryService movieInventoryService) {
        this.movieInventoryService = movieInventoryService;
    }

    /**
     * {@code POST  /movie-inventories} : Create a new movieInventory.
     *
     * @param movieInventoryDTO the movieInventoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new movieInventoryDTO, or with status {@code 400 (Bad Request)} if the movieInventory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/movie-inventories")
    public ResponseEntity<MovieInventoryDTO> createMovieInventory(@Valid @RequestBody MovieInventoryDTO movieInventoryDTO) throws URISyntaxException {
        log.debug("REST request to save MovieInventory : {}", movieInventoryDTO);
        if (movieInventoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new movieInventory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MovieInventoryDTO result = movieInventoryService.save(movieInventoryDTO);
        return ResponseEntity.created(new URI("/api/movie-inventories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /movie-inventories} : Updates an existing movieInventory.
     *
     * @param movieInventoryDTO the movieInventoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated movieInventoryDTO,
     * or with status {@code 400 (Bad Request)} if the movieInventoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the movieInventoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/movie-inventories")
    public ResponseEntity<MovieInventoryDTO> updateMovieInventory(@Valid @RequestBody MovieInventoryDTO movieInventoryDTO) throws URISyntaxException {
        log.debug("REST request to update MovieInventory : {}", movieInventoryDTO);
        if (movieInventoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MovieInventoryDTO result = movieInventoryService.save(movieInventoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, movieInventoryDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /movie-inventories} : get all the movieInventories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of movieInventories in body.
     */
    @GetMapping("/movie-inventories")
    public ResponseEntity<List<MovieInventoryDTO>> getAllMovieInventories(Pageable pageable) {
        log.debug("REST request to get a page of MovieInventories");
        Page<MovieInventoryDTO> page = movieInventoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /movie-inventories/:id} : get the "id" movieInventory.
     *
     * @param id the id of the movieInventoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the movieInventoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/movie-inventories/{id}")
    public ResponseEntity<MovieInventoryDTO> getMovieInventory(@PathVariable Long id) {
        log.debug("REST request to get MovieInventory : {}", id);
        Optional<MovieInventoryDTO> movieInventoryDTO = movieInventoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(movieInventoryDTO);
    }

    /**
     * {@code DELETE  /movie-inventories/:id} : delete the "id" movieInventory.
     *
     * @param id the id of the movieInventoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/movie-inventories/{id}")
    public ResponseEntity<Void> deleteMovieInventory(@PathVariable Long id) {
        log.debug("REST request to delete MovieInventory : {}", id);
        movieInventoryService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/movie-inventories?query=:query} : search for the movieInventory corresponding
     * to the query.
     *
     * @param query the query of the movieInventory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/movie-inventories")
    public ResponseEntity<List<MovieInventoryDTO>> searchMovieInventories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of MovieInventories for query {}", query);
        Page<MovieInventoryDTO> page = movieInventoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
