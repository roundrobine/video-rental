package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.VideoRentalApp;
import com.roundrobine.movie.rentals.domain.RentedCopy;
import com.roundrobine.movie.rentals.domain.MovieInventory;
import com.roundrobine.movie.rentals.domain.RentalOrder;
import com.roundrobine.movie.rentals.repository.RentedCopyRepository;
import com.roundrobine.movie.rentals.repository.search.RentedCopySearchRepository;
import com.roundrobine.movie.rentals.service.RentedCopyService;
import com.roundrobine.movie.rentals.service.dto.RentedCopyDTO;
import com.roundrobine.movie.rentals.service.mapper.RentedCopyMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RentedCopyResource} REST controller.
 */
@SpringBootTest(classes = VideoRentalApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class RentedCopyResourceIT {

    private static final Instant DEFAULT_RENT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RENT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_PLANNED_RENT_DURATION = 1;
    private static final Integer UPDATED_PLANNED_RENT_DURATION = 2;

    private static final Integer DEFAULT_EXTRA_CHARGED_DAYS = 1;
    private static final Integer UPDATED_EXTRA_CHARGED_DAYS = 2;

    private static final Instant DEFAULT_RETURN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RETURN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RentedCopyRepository rentedCopyRepository;

    @Autowired
    private RentedCopyMapper rentedCopyMapper;

    @Autowired
    private RentedCopyService rentedCopyService;

    /**
     * This repository is mocked in the com.roundrobine.movie.rentals.repository.search test package.
     *
     * @see com.roundrobine.movie.rentals.repository.search.RentedCopySearchRepositoryMockConfiguration
     */
    @Autowired
    private RentedCopySearchRepository mockRentedCopySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRentedCopyMockMvc;

    private RentedCopy rentedCopy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentedCopy createEntity(EntityManager em) {
        RentedCopy rentedCopy = RentedCopy.builder()
            .rentDate(DEFAULT_RENT_DATE)
            .plannedRentDuration(DEFAULT_PLANNED_RENT_DURATION)
            .extraChargedDays(DEFAULT_EXTRA_CHARGED_DAYS)
            .returnDate(DEFAULT_RETURN_DATE).build();
        // Add required entity
        MovieInventory movieInventory;
        if (TestUtil.findAll(em, MovieInventory.class).isEmpty()) {
            movieInventory = MovieInventoryResourceIT.createEntity(em);
            em.persist(movieInventory);
            em.flush();
        } else {
            movieInventory = TestUtil.findAll(em, MovieInventory.class).get(0);
        }
        rentedCopy.setMovieInventory(movieInventory);
        // Add required entity
        RentalOrder rentalOrder;
        if (TestUtil.findAll(em, RentalOrder.class).isEmpty()) {
            rentalOrder = RentalOrderResourceIT.createEntity(em);
            em.persist(rentalOrder);
            em.flush();
        } else {
            rentalOrder = TestUtil.findAll(em, RentalOrder.class).get(0);
        }
        rentedCopy.setOrder(rentalOrder);
        return rentedCopy;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentedCopy createUpdatedEntity(EntityManager em) {
        RentedCopy rentedCopy = RentedCopy.builder()
            .rentDate(UPDATED_RENT_DATE)
            .plannedRentDuration(UPDATED_PLANNED_RENT_DURATION)
            .extraChargedDays(UPDATED_EXTRA_CHARGED_DAYS)
            .returnDate(UPDATED_RETURN_DATE).build();
        // Add required entity
        MovieInventory movieInventory;
        if (TestUtil.findAll(em, MovieInventory.class).isEmpty()) {
            movieInventory = MovieInventoryResourceIT.createUpdatedEntity(em);
            em.persist(movieInventory);
            em.flush();
        } else {
            movieInventory = TestUtil.findAll(em, MovieInventory.class).get(0);
        }
        rentedCopy.setMovieInventory(movieInventory);
        // Add required entity
        RentalOrder rentalOrder;
        if (TestUtil.findAll(em, RentalOrder.class).isEmpty()) {
            rentalOrder = RentalOrderResourceIT.createUpdatedEntity(em);
            em.persist(rentalOrder);
            em.flush();
        } else {
            rentalOrder = TestUtil.findAll(em, RentalOrder.class).get(0);
        }
        rentedCopy.setOrder(rentalOrder);
        return rentedCopy;
    }

    @BeforeEach
    public void initTest() {
        rentedCopy = createEntity(em);
    }

    @Test
    @Transactional
    public void createRentedCopy() throws Exception {
        int databaseSizeBeforeCreate = rentedCopyRepository.findAll().size();
        // Create the RentedCopy
        RentedCopyDTO rentedCopyDTO = rentedCopyMapper.toDto(rentedCopy);
        restRentedCopyMockMvc.perform(post("/api/rented-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentedCopyDTO)))
            .andExpect(status().isCreated());

        // Validate the RentedCopy in the database
        List<RentedCopy> rentedCopyList = rentedCopyRepository.findAll();
        assertThat(rentedCopyList).hasSize(databaseSizeBeforeCreate + 1);
        RentedCopy testRentedCopy = rentedCopyList.get(rentedCopyList.size() - 1);
        assertThat(testRentedCopy.getRentDate()).isEqualTo(DEFAULT_RENT_DATE);
        assertThat(testRentedCopy.getPlannedRentDuration()).isEqualTo(DEFAULT_PLANNED_RENT_DURATION);
        assertThat(testRentedCopy.getExtraChargedDays()).isEqualTo(DEFAULT_EXTRA_CHARGED_DAYS);
        assertThat(testRentedCopy.getReturnDate()).isEqualTo(DEFAULT_RETURN_DATE);

        // Validate the RentedCopy in Elasticsearch
        verify(mockRentedCopySearchRepository, times(1)).save(testRentedCopy);
    }

    @Test
    @Transactional
    public void createRentedCopyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rentedCopyRepository.findAll().size();

        // Create the RentedCopy with an existing ID
        rentedCopy.setId(1L);
        RentedCopyDTO rentedCopyDTO = rentedCopyMapper.toDto(rentedCopy);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRentedCopyMockMvc.perform(post("/api/rented-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentedCopyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RentedCopy in the database
        List<RentedCopy> rentedCopyList = rentedCopyRepository.findAll();
        assertThat(rentedCopyList).hasSize(databaseSizeBeforeCreate);

        // Validate the RentedCopy in Elasticsearch
        verify(mockRentedCopySearchRepository, times(0)).save(rentedCopy);
    }


    @Test
    @Transactional
    public void checkRentDateIsAlwaysSet() throws Exception {
        int databaseSizeBeforeTest = rentedCopyRepository.findAll().size();
        // set the field null
        rentedCopy.setRentDate(null);

        // Create the RentedCopy, which fails.
        RentedCopyDTO rentedCopyDTO = rentedCopyMapper.toDto(rentedCopy);


        restRentedCopyMockMvc.perform(post("/api/rented-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentedCopyDTO)))
            .andExpect(status().isCreated());

    }

    @Test
    @Transactional
    public void checkPlannedRentDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentedCopyRepository.findAll().size();
        // set the field null
        rentedCopy.setPlannedRentDuration(null);

        // Create the RentedCopy, which fails.
        RentedCopyDTO rentedCopyDTO = rentedCopyMapper.toDto(rentedCopy);


        restRentedCopyMockMvc.perform(post("/api/rented-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentedCopyDTO)))
            .andExpect(status().isBadRequest());

        List<RentedCopy> rentedCopyList = rentedCopyRepository.findAll();
        assertThat(rentedCopyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRentedCopies() throws Exception {
        // Initialize the database
        rentedCopyRepository.saveAndFlush(rentedCopy);

        // Get all the rentedCopyList
        restRentedCopyMockMvc.perform(get("/api/rented-copies?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rentedCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].rentDate").value(hasItem(DEFAULT_RENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedRentDuration").value(hasItem(DEFAULT_PLANNED_RENT_DURATION)))
            .andExpect(jsonPath("$.[*].extraChargedDays").value(hasItem(DEFAULT_EXTRA_CHARGED_DAYS)))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getRentedCopy() throws Exception {
        // Initialize the database
        rentedCopyRepository.saveAndFlush(rentedCopy);

        // Get the rentedCopy
        restRentedCopyMockMvc.perform(get("/api/rented-copies/{id}", rentedCopy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rentedCopy.getId().intValue()))
            .andExpect(jsonPath("$.rentDate").value(DEFAULT_RENT_DATE.toString()))
            .andExpect(jsonPath("$.plannedRentDuration").value(DEFAULT_PLANNED_RENT_DURATION))
            .andExpect(jsonPath("$.extraChargedDays").value(DEFAULT_EXTRA_CHARGED_DAYS))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingRentedCopy() throws Exception {
        // Get the rentedCopy
        restRentedCopyMockMvc.perform(get("/api/rented-copies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRentedCopy() throws Exception {
        // Initialize the database
        rentedCopyRepository.saveAndFlush(rentedCopy);

        int databaseSizeBeforeUpdate = rentedCopyRepository.findAll().size();

        // Update the rentedCopy
        RentedCopy updatedRentedCopy = rentedCopyRepository.findById(rentedCopy.getId()).get();
        // Disconnect from session so that the updates on updatedRentedCopy are not directly saved in db
        em.detach(updatedRentedCopy);
        updatedRentedCopy = updatedRentedCopy.toBuilder()
            .rentDate(UPDATED_RENT_DATE)
            .plannedRentDuration(UPDATED_PLANNED_RENT_DURATION)
            .extraChargedDays(UPDATED_EXTRA_CHARGED_DAYS)
            .returnDate(UPDATED_RETURN_DATE)
            .movieInventory(updatedRentedCopy.getMovieInventory()).build();
        RentedCopyDTO rentedCopyDTO = rentedCopyMapper.toDto(updatedRentedCopy);

        restRentedCopyMockMvc.perform(put("/api/rented-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentedCopyDTO)))
            .andExpect(status().isOk());

        // Validate the RentedCopy in the database
        List<RentedCopy> rentedCopyList = rentedCopyRepository.findAll();
        assertThat(rentedCopyList).hasSize(databaseSizeBeforeUpdate);
        RentedCopy testRentedCopy = rentedCopyList.get(rentedCopyList.size() - 1);
        assertThat(testRentedCopy.getRentDate()).isEqualTo(UPDATED_RENT_DATE);
        assertThat(testRentedCopy.getPlannedRentDuration()).isEqualTo(UPDATED_PLANNED_RENT_DURATION);
        assertThat(testRentedCopy.getExtraChargedDays()).isEqualTo(UPDATED_EXTRA_CHARGED_DAYS);
        assertThat(testRentedCopy.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);

        // Validate the RentedCopy in Elasticsearch
        verify(mockRentedCopySearchRepository, times(1)).save(testRentedCopy);
    }

    @Test
    @Transactional
    public void updateNonExistingRentedCopy() throws Exception {
        int databaseSizeBeforeUpdate = rentedCopyRepository.findAll().size();

        // Create the RentedCopy
        RentedCopyDTO rentedCopyDTO = rentedCopyMapper.toDto(rentedCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRentedCopyMockMvc.perform(put("/api/rented-copies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentedCopyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RentedCopy in the database
        List<RentedCopy> rentedCopyList = rentedCopyRepository.findAll();
        assertThat(rentedCopyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RentedCopy in Elasticsearch
        verify(mockRentedCopySearchRepository, times(0)).save(rentedCopy);
    }

    @Test
    @Transactional
    public void deleteRentedCopy() throws Exception {
        // Initialize the database
        rentedCopyRepository.saveAndFlush(rentedCopy);

        int databaseSizeBeforeDelete = rentedCopyRepository.findAll().size();

        // Delete the rentedCopy
        restRentedCopyMockMvc.perform(delete("/api/rented-copies/{id}", rentedCopy.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RentedCopy> rentedCopyList = rentedCopyRepository.findAll();
        assertThat(rentedCopyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RentedCopy in Elasticsearch
        verify(mockRentedCopySearchRepository, times(1)).deleteById(rentedCopy.getId());
    }

    @Test
    @Transactional
    public void searchRentedCopy() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        rentedCopyRepository.saveAndFlush(rentedCopy);
        when(mockRentedCopySearchRepository.search(queryStringQuery("id:" + rentedCopy.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(rentedCopy), PageRequest.of(0, 1), 1));

        // Search the rentedCopy
        restRentedCopyMockMvc.perform(get("/api/_search/rented-copies?query=id:" + rentedCopy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rentedCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].rentDate").value(hasItem(DEFAULT_RENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedRentDuration").value(hasItem(DEFAULT_PLANNED_RENT_DURATION)))
            .andExpect(jsonPath("$.[*].extraChargedDays").value(hasItem(DEFAULT_EXTRA_CHARGED_DAYS)))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));
    }
}
