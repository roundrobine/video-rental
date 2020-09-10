package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.domain.User;
import com.roundrobine.movie.rentals.service.RentalOrderService;
import com.roundrobine.movie.rentals.service.UserService;
import com.roundrobine.movie.rentals.service.dto.CreateRentalOrderDTO;
import com.roundrobine.movie.rentals.web.rest.errors.BadRequestAlertException;
import com.roundrobine.movie.rentals.service.dto.RentalOrderDTO;

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
 * REST controller for managing {@link com.roundrobine.movie.rentals.domain.RentalOrder}.
 */
@RestController
@RequestMapping("/api")
public class RentalOrderResource {

    private final Logger log = LoggerFactory.getLogger(RentalOrderResource.class);

    private static final String ENTITY_NAME = "rentalOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RentalOrderService rentalOrderService;
    private final UserService userService;

    public RentalOrderResource(RentalOrderService rentalOrderService, UserService userService) {
        this.rentalOrderService = rentalOrderService;
        this.userService = userService;
    }


    /**
     * {@code POST  /rental-orders} : Create a new rentalOrder.
     *
     * @param createRentalOrderDTO the rentalOrderDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rentalOrderDTO, or
     * with status {@code 400 (Bad Request)} if the rentalOrder can not be created
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rental-orders")
    public ResponseEntity<RentalOrderDTO> createRentalOrder(@Valid @RequestBody CreateRentalOrderDTO createRentalOrderDTO)
        throws URISyntaxException {
        log.debug("REST request to create a full RentalOrder : {}", createRentalOrderDTO);

        final Optional<User> isUser = userService.getUserWithAuthorities();
        if(!isUser.isPresent()) {
            log.error("User is not logged in");
            throw new BadRequestAlertException("User does not exist!", ENTITY_NAME, "userunavailable");
        }

        RentalOrderDTO result = rentalOrderService.processRentalOrder(isUser.get(),createRentalOrderDTO);
        return ResponseEntity.created(new URI("/api/rental-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /rental-orders} : Updates an existing rentalOrder.
     *
     * @param rentalOrderDTO the rentalOrderDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rentalOrderDTO,
     * or with status {@code 400 (Bad Request)} if the rentalOrderDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rentalOrderDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rental-orders")
    public ResponseEntity<RentalOrderDTO> updateRentalOrder(@Valid @RequestBody RentalOrderDTO rentalOrderDTO) throws URISyntaxException {
        log.debug("REST request to update RentalOrder : {}", rentalOrderDTO);
        if (rentalOrderDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RentalOrderDTO result = rentalOrderService.save(rentalOrderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, rentalOrderDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /rental-orders} : get all the rentalOrders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rentalOrders in body.
     */
    @GetMapping("/rental-orders")
    public ResponseEntity<List<RentalOrderDTO>> getAllRentalOrders(Pageable pageable) {
        log.debug("REST request to get a page of RentalOrders");
        Page<RentalOrderDTO> page = rentalOrderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rental-orders/:id} : get the "id" rentalOrder.
     *
     * @param id the id of the rentalOrderDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rentalOrderDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rental-orders/{id}")
    public ResponseEntity<RentalOrderDTO> getRentalOrder(@PathVariable Long id) {
        log.debug("REST request to get RentalOrder : {}", id);
        Optional<RentalOrderDTO> rentalOrderDTO = rentalOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rentalOrderDTO);
    }

    /**
     * {@code DELETE  /rental-orders/:id} : delete the "id" rentalOrder.
     *
     * @param id the id of the rentalOrderDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rental-orders/{id}")
    public ResponseEntity<Void> deleteRentalOrder(@PathVariable Long id) {
        log.debug("REST request to delete RentalOrder : {}", id);
        rentalOrderService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/rental-orders?query=:query} : search for the rentalOrder corresponding
     * to the query.
     *
     * @param query the query of the rentalOrder search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/rental-orders")
    public ResponseEntity<List<RentalOrderDTO>> searchRentalOrders(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of RentalOrders for query {}", query);
        Page<RentalOrderDTO> page = rentalOrderService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
        }
}
