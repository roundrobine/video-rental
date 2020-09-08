package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.VideoRentalApp;
import com.roundrobine.movie.rentals.domain.MovieInventory;
import com.roundrobine.movie.rentals.domain.Movie;
import com.roundrobine.movie.rentals.repository.MovieInventoryRepository;
import com.roundrobine.movie.rentals.repository.search.MovieInventorySearchRepository;
import com.roundrobine.movie.rentals.service.MovieInventoryService;
import com.roundrobine.movie.rentals.service.dto.MovieInventoryDTO;
import com.roundrobine.movie.rentals.service.mapper.MovieInventoryMapper;

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

import com.roundrobine.movie.rentals.domain.enumeration.RentalStatus;
/**
 * Integration tests for the {@link MovieInventoryResource} REST controller.
 */
@SpringBootTest(classes = VideoRentalApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class MovieInventoryResourceIT {

    private static final Instant DEFAULT_LAST_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final RentalStatus DEFAULT_STATUS = RentalStatus.AVAILABLE;
    private static final RentalStatus UPDATED_STATUS = RentalStatus.RENTED;

    @Autowired
    private MovieInventoryRepository movieInventoryRepository;

    @Autowired
    private MovieInventoryMapper movieInventoryMapper;

    @Autowired
    private MovieInventoryService movieInventoryService;

    /**
     * This repository is mocked in the com.roundrobine.movie.rentals.repository.search test package.
     *
     * @see com.roundrobine.movie.rentals.repository.search.MovieInventorySearchRepositoryMockConfiguration
     */
    @Autowired
    private MovieInventorySearchRepository mockMovieInventorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMovieInventoryMockMvc;

    private MovieInventory movieInventory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MovieInventory createEntity(EntityManager em) {
        MovieInventory movieInventory = MovieInventory.builder()
            .lastUpdatedAt(DEFAULT_LAST_UPDATED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .status(DEFAULT_STATUS).build();
        // Add required entity
        Movie movie;
        if (TestUtil.findAll(em, Movie.class).isEmpty()) {
            movie = MovieResourceIT.createEntity(em);
            em.persist(movie);
            em.flush();
        } else {
            movie = TestUtil.findAll(em, Movie.class).get(0);
        }
        movieInventory.setMovie(movie);
        return movieInventory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MovieInventory createUpdatedEntity(EntityManager em) {
        MovieInventory movieInventory = MovieInventory.builder()
            .lastUpdatedAt(UPDATED_LAST_UPDATED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .status(UPDATED_STATUS).build();
        // Add required entity
        Movie movie;
        if (TestUtil.findAll(em, Movie.class).isEmpty()) {
            movie = MovieResourceIT.createUpdatedEntity(em);
            em.persist(movie);
            em.flush();
        } else {
            movie = TestUtil.findAll(em, Movie.class).get(0);
        }
        movieInventory.setMovie(movie);
        return movieInventory;
    }

    @BeforeEach
    public void initTest() {
        movieInventory = createEntity(em);
    }

    @Test
    @Transactional
    public void createMovieInventory() throws Exception {
        int databaseSizeBeforeCreate = movieInventoryRepository.findAll().size();
        // Create the MovieInventory
        MovieInventoryDTO movieInventoryDTO = movieInventoryMapper.toDto(movieInventory);
        restMovieInventoryMockMvc.perform(post("/api/movie-inventories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movieInventoryDTO)))
            .andExpect(status().isCreated());

        // Validate the MovieInventory in the database
        List<MovieInventory> movieInventoryList = movieInventoryRepository.findAll();
        assertThat(movieInventoryList).hasSize(databaseSizeBeforeCreate + 1);
        MovieInventory testMovieInventory = movieInventoryList.get(movieInventoryList.size() - 1);
        assertThat(testMovieInventory.getLastUpdatedAt()).isAfter(DEFAULT_LAST_UPDATED_AT);
        assertThat(testMovieInventory.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testMovieInventory.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the MovieInventory in Elasticsearch
        verify(mockMovieInventorySearchRepository, times(1)).save(testMovieInventory);
    }

    @Test
    @Transactional
    public void createMovieInventoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = movieInventoryRepository.findAll().size();

        // Create the MovieInventory with an existing ID
        movieInventory.setId(1L);
        MovieInventoryDTO movieInventoryDTO = movieInventoryMapper.toDto(movieInventory);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMovieInventoryMockMvc.perform(post("/api/movie-inventories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movieInventoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MovieInventory in the database
        List<MovieInventory> movieInventoryList = movieInventoryRepository.findAll();
        assertThat(movieInventoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the MovieInventory in Elasticsearch
        verify(mockMovieInventorySearchRepository, times(0)).save(movieInventory);
    }




    @Test
    @Transactional
    public void getAllMovieInventories() throws Exception {
        // Initialize the database
        movieInventoryRepository.saveAndFlush(movieInventory);

        // Get all the movieInventoryList
        restMovieInventoryMockMvc.perform(get("/api/movie-inventories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movieInventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastUpdatedAt").value(hasItem(DEFAULT_LAST_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getMovieInventory() throws Exception {
        // Initialize the database
        movieInventoryRepository.saveAndFlush(movieInventory);

        // Get the movieInventory
        restMovieInventoryMockMvc.perform(get("/api/movie-inventories/{id}", movieInventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(movieInventory.getId().intValue()))
            .andExpect(jsonPath("$.lastUpdatedAt").value(DEFAULT_LAST_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingMovieInventory() throws Exception {
        // Get the movieInventory
        restMovieInventoryMockMvc.perform(get("/api/movie-inventories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMovieInventory() throws Exception {
        // Initialize the database
        movieInventoryRepository.saveAndFlush(movieInventory);

        int databaseSizeBeforeUpdate = movieInventoryRepository.findAll().size();

        // Update the movieInventory
        MovieInventory updatedMovieInventory = movieInventoryRepository.findById(movieInventory.getId()).get();
        // Disconnect from session so that the updates on updatedMovieInventory are not directly saved in db
        em.detach(updatedMovieInventory);
        updatedMovieInventory = updatedMovieInventory.toBuilder()
            .lastUpdatedAt(UPDATED_LAST_UPDATED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .status(UPDATED_STATUS).build();
        MovieInventoryDTO movieInventoryDTO = movieInventoryMapper.toDto(updatedMovieInventory);

        restMovieInventoryMockMvc.perform(put("/api/movie-inventories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movieInventoryDTO)))
            .andExpect(status().isOk());

        // Validate the MovieInventory in the database
        List<MovieInventory> movieInventoryList = movieInventoryRepository.findAll();
        assertThat(movieInventoryList).hasSize(databaseSizeBeforeUpdate);
        MovieInventory testMovieInventory = movieInventoryList.get(movieInventoryList.size() - 1);
        assertThat(testMovieInventory.getLastUpdatedAt()).isAfter(UPDATED_LAST_UPDATED_AT);
        assertThat(testMovieInventory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testMovieInventory.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the MovieInventory in Elasticsearch
        verify(mockMovieInventorySearchRepository, times(1)).save(testMovieInventory);
    }

    @Test
    @Transactional
    public void updateNonExistingMovieInventory() throws Exception {
        int databaseSizeBeforeUpdate = movieInventoryRepository.findAll().size();

        // Create the MovieInventory
        MovieInventoryDTO movieInventoryDTO = movieInventoryMapper.toDto(movieInventory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMovieInventoryMockMvc.perform(put("/api/movie-inventories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(movieInventoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MovieInventory in the database
        List<MovieInventory> movieInventoryList = movieInventoryRepository.findAll();
        assertThat(movieInventoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the MovieInventory in Elasticsearch
        verify(mockMovieInventorySearchRepository, times(0)).save(movieInventory);
    }

    @Test
    @Transactional
    public void deleteMovieInventory() throws Exception {
        // Initialize the database
        movieInventoryRepository.saveAndFlush(movieInventory);

        int databaseSizeBeforeDelete = movieInventoryRepository.findAll().size();

        // Delete the movieInventory
        restMovieInventoryMockMvc.perform(delete("/api/movie-inventories/{id}", movieInventory.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<MovieInventory> movieInventoryList = movieInventoryRepository.findAll();
        assertThat(movieInventoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the MovieInventory in Elasticsearch
        verify(mockMovieInventorySearchRepository, times(1)).deleteById(movieInventory.getId());
    }

    @Test
    @Transactional
    public void searchMovieInventory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        movieInventoryRepository.saveAndFlush(movieInventory);
        when(mockMovieInventorySearchRepository.search(queryStringQuery("id:" + movieInventory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(movieInventory), PageRequest.of(0, 1), 1));

        // Search the movieInventory
        restMovieInventoryMockMvc.perform(get("/api/_search/movie-inventories?query=id:" + movieInventory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(movieInventory.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastUpdatedAt").value(hasItem(DEFAULT_LAST_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
}
