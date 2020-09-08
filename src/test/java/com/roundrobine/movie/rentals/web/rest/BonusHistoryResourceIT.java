package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.VideoRentalApp;
import com.roundrobine.movie.rentals.domain.BonusHistory;
import com.roundrobine.movie.rentals.domain.Customer;
import com.roundrobine.movie.rentals.domain.RentalOrder;
import com.roundrobine.movie.rentals.repository.BonusHistoryRepository;
import com.roundrobine.movie.rentals.repository.search.BonusHistorySearchRepository;
import com.roundrobine.movie.rentals.service.BonusHistoryService;
import com.roundrobine.movie.rentals.service.dto.BonusHistoryDTO;
import com.roundrobine.movie.rentals.service.mapper.BonusHistoryMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 * Integration tests for the {@link BonusHistoryResource} REST controller.
 */
@SpringBootTest(classes = VideoRentalApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class BonusHistoryResourceIT {

    private static final Long DEFAULT_POINTS = 1L;
    private static final Long UPDATED_POINTS = 2L;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private BonusHistoryRepository bonusHistoryRepository;

    @Autowired
    private BonusHistoryMapper bonusHistoryMapper;

    @Autowired
    private BonusHistoryService bonusHistoryService;

    /**
     * This repository is mocked in the com.roundrobine.movie.rentals.repository.search test package.
     *
     * @see com.roundrobine.movie.rentals.repository.search.BonusHistorySearchRepositoryMockConfiguration
     */
    @Autowired
    private BonusHistorySearchRepository mockBonusHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBonusHistoryMockMvc;

    private BonusHistory bonusHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BonusHistory createEntity(EntityManager em) {
        BonusHistory bonusHistory = BonusHistory.builder()
            .points(DEFAULT_POINTS)
            .createdAt(DEFAULT_CREATED_AT).build();
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        bonusHistory.setCustomer(customer);
        // Add required entity
        RentalOrder rentalOrder;
        if (TestUtil.findAll(em, RentalOrder.class).isEmpty()) {
            rentalOrder = RentalOrderResourceIT.createEntity(em);
            em.persist(rentalOrder);
            em.flush();
        } else {
            rentalOrder = TestUtil.findAll(em, RentalOrder.class).get(0);
        }
        bonusHistory.setOder(rentalOrder);
        return bonusHistory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BonusHistory createUpdatedEntity(EntityManager em) {
        BonusHistory bonusHistory = BonusHistory.builder()
            .points(UPDATED_POINTS)
            .createdAt(UPDATED_CREATED_AT).build();
        // Add required entity
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            customer = CustomerResourceIT.createUpdatedEntity(em);
            em.persist(customer);
            em.flush();
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        bonusHistory.setCustomer(customer);
        // Add required entity
        RentalOrder rentalOrder;
        if (TestUtil.findAll(em, RentalOrder.class).isEmpty()) {
            rentalOrder = RentalOrderResourceIT.createUpdatedEntity(em);
            em.persist(rentalOrder);
            em.flush();
        } else {
            rentalOrder = TestUtil.findAll(em, RentalOrder.class).get(0);
        }
        bonusHistory.setOder(rentalOrder);
        return bonusHistory;
    }

    @BeforeEach
    public void initTest() {
        bonusHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createBonusHistory() throws Exception {
        int databaseSizeBeforeCreate = bonusHistoryRepository.findAll().size();
        // Create the BonusHistory
        BonusHistoryDTO bonusHistoryDTO = bonusHistoryMapper.toDto(bonusHistory);
        restBonusHistoryMockMvc.perform(post("/api/bonus-histories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bonusHistoryDTO)))
            .andExpect(status().isCreated());

        // Validate the BonusHistory in the database
        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        BonusHistory testBonusHistory = bonusHistoryList.get(bonusHistoryList.size() - 1);
        assertThat(testBonusHistory.getPoints()).isEqualTo(DEFAULT_POINTS);
        assertThat(testBonusHistory.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);

        // Validate the BonusHistory in Elasticsearch
        verify(mockBonusHistorySearchRepository, times(1)).save(testBonusHistory);
    }

    @Test
    @Transactional
    public void createBonusHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bonusHistoryRepository.findAll().size();

        // Create the BonusHistory with an existing ID
        bonusHistory.setId(1L);
        BonusHistoryDTO bonusHistoryDTO = bonusHistoryMapper.toDto(bonusHistory);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBonusHistoryMockMvc.perform(post("/api/bonus-histories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bonusHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BonusHistory in the database
        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeCreate);

        // Validate the BonusHistory in Elasticsearch
        verify(mockBonusHistorySearchRepository, times(0)).save(bonusHistory);
    }


    @Test
    @Transactional
    public void checkPointsIsRequired() throws Exception {
        int databaseSizeBeforeTest = bonusHistoryRepository.findAll().size();
        // set the field null
        bonusHistory.setPoints(null);

        // Create the BonusHistory, which fails.
        BonusHistoryDTO bonusHistoryDTO = bonusHistoryMapper.toDto(bonusHistory);


        restBonusHistoryMockMvc.perform(post("/api/bonus-histories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bonusHistoryDTO)))
            .andExpect(status().isBadRequest());

        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = bonusHistoryRepository.findAll().size();
        // set the field null
        bonusHistory.setCreatedAt(null);

        // Create the BonusHistory, which fails.
        BonusHistoryDTO bonusHistoryDTO = bonusHistoryMapper.toDto(bonusHistory);


        restBonusHistoryMockMvc.perform(post("/api/bonus-histories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bonusHistoryDTO)))
            .andExpect(status().isBadRequest());

        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBonusHistories() throws Exception {
        // Initialize the database
        bonusHistoryRepository.saveAndFlush(bonusHistory);

        // Get all the bonusHistoryList
        restBonusHistoryMockMvc.perform(get("/api/bonus-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bonusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }
    
    @Test
    @Transactional
    public void getBonusHistory() throws Exception {
        // Initialize the database
        bonusHistoryRepository.saveAndFlush(bonusHistory);

        // Get the bonusHistory
        restBonusHistoryMockMvc.perform(get("/api/bonus-histories/{id}", bonusHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bonusHistory.getId().intValue()))
            .andExpect(jsonPath("$.points").value(DEFAULT_POINTS.intValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingBonusHistory() throws Exception {
        // Get the bonusHistory
        restBonusHistoryMockMvc.perform(get("/api/bonus-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBonusHistory() throws Exception {
        // Initialize the database
        bonusHistoryRepository.saveAndFlush(bonusHistory);

        int databaseSizeBeforeUpdate = bonusHistoryRepository.findAll().size();

        // Update the bonusHistory
        BonusHistory updatedBonusHistory = bonusHistoryRepository.findById(bonusHistory.getId()).get();
        // Disconnect from session so that the updates on updatedBonusHistory are not directly saved in db
        em.detach(updatedBonusHistory);
        updatedBonusHistory = updatedBonusHistory.toBuilder()
            .points(UPDATED_POINTS)
            .createdAt(UPDATED_CREATED_AT).build();
        BonusHistoryDTO bonusHistoryDTO = bonusHistoryMapper.toDto(updatedBonusHistory);

        restBonusHistoryMockMvc.perform(put("/api/bonus-histories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bonusHistoryDTO)))
            .andExpect(status().isOk());

        // Validate the BonusHistory in the database
        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeUpdate);
        BonusHistory testBonusHistory = bonusHistoryList.get(bonusHistoryList.size() - 1);
        assertThat(testBonusHistory.getPoints()).isEqualTo(UPDATED_POINTS);
        assertThat(testBonusHistory.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);

        // Validate the BonusHistory in Elasticsearch
        verify(mockBonusHistorySearchRepository, times(1)).save(testBonusHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingBonusHistory() throws Exception {
        int databaseSizeBeforeUpdate = bonusHistoryRepository.findAll().size();

        // Create the BonusHistory
        BonusHistoryDTO bonusHistoryDTO = bonusHistoryMapper.toDto(bonusHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBonusHistoryMockMvc.perform(put("/api/bonus-histories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bonusHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BonusHistory in the database
        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BonusHistory in Elasticsearch
        verify(mockBonusHistorySearchRepository, times(0)).save(bonusHistory);
    }

    @Test
    @Transactional
    public void deleteBonusHistory() throws Exception {
        // Initialize the database
        bonusHistoryRepository.saveAndFlush(bonusHistory);

        int databaseSizeBeforeDelete = bonusHistoryRepository.findAll().size();

        // Delete the bonusHistory
        restBonusHistoryMockMvc.perform(delete("/api/bonus-histories/{id}", bonusHistory.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BonusHistory> bonusHistoryList = bonusHistoryRepository.findAll();
        assertThat(bonusHistoryList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the BonusHistory in Elasticsearch
        verify(mockBonusHistorySearchRepository, times(1)).deleteById(bonusHistory.getId());
    }

    @Test
    @Transactional
    public void searchBonusHistory() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        bonusHistoryRepository.saveAndFlush(bonusHistory);
        when(mockBonusHistorySearchRepository.search(queryStringQuery("id:" + bonusHistory.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(bonusHistory), PageRequest.of(0, 1), 1));

        // Search the bonusHistory
        restBonusHistoryMockMvc.perform(get("/api/_search/bonus-histories?query=id:" + bonusHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bonusHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].points").value(hasItem(DEFAULT_POINTS.intValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }
}
