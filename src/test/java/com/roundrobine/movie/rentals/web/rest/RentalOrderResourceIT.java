package com.roundrobine.movie.rentals.web.rest;

import com.roundrobine.movie.rentals.VideoRentalApp;
import com.roundrobine.movie.rentals.domain.RentalOrder;
import com.roundrobine.movie.rentals.domain.Customer;
import com.roundrobine.movie.rentals.repository.RentalOrderRepository;
import com.roundrobine.movie.rentals.repository.search.RentalOrderSearchRepository;
import com.roundrobine.movie.rentals.service.RentalOrderService;
import com.roundrobine.movie.rentals.service.dto.RentalOrderDTO;
import com.roundrobine.movie.rentals.service.mapper.RentalOrderMapper;

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
import java.math.BigDecimal;
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

import com.roundrobine.movie.rentals.domain.enumeration.Currency;
import com.roundrobine.movie.rentals.domain.enumeration.OrderStatus;
/**
 * Integration tests for the {@link RentalOrderResource} REST controller.
 */
@SpringBootTest(classes = VideoRentalApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class RentalOrderResourceIT {

    private static final BigDecimal DEFAULT_LATE_CHARGED_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_LATE_CHARGED_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);

    private static final Currency DEFAULT_CURENCY = Currency.SEK;
    private static final Currency UPDATED_CURENCY = Currency.SEK;

    private static final OrderStatus DEFAULT_STATUS = OrderStatus.NEW;
    private static final OrderStatus UPDATED_STATUS = OrderStatus.ACTIVE;

    private static final Instant DEFAULT_LAST_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RentalOrderRepository rentalOrderRepository;

    @Autowired
    private RentalOrderMapper rentalOrderMapper;

    @Autowired
    private RentalOrderService rentalOrderService;

    /**
     * This repository is mocked in the com.roundrobine.movie.rentals.repository.search test package.
     *
     * @see com.roundrobine.movie.rentals.repository.search.RentalOrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private RentalOrderSearchRepository mockRentalOrderSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRentalOrderMockMvc;

    private RentalOrder rentalOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentalOrder createEntity(EntityManager em) {
        RentalOrder rentalOrder =  RentalOrder.builder()
            .lateChargedAmount(DEFAULT_LATE_CHARGED_AMOUNT)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .currency(DEFAULT_CURENCY)
            .status(DEFAULT_STATUS)
            .lastUpdatedAt(DEFAULT_LAST_UPDATED_AT)
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
        rentalOrder.setCustomer(customer);
        return rentalOrder;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RentalOrder createUpdatedEntity(EntityManager em) {
        RentalOrder rentalOrder = RentalOrder.builder()
            .lateChargedAmount(UPDATED_LATE_CHARGED_AMOUNT)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .currency(UPDATED_CURENCY)
            .status(UPDATED_STATUS)
            .lastUpdatedAt(UPDATED_LAST_UPDATED_AT)
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
        rentalOrder.setCustomer(customer);
        return rentalOrder;
    }

    @BeforeEach
    public void initTest() {
        rentalOrder = createEntity(em);
    }


    @Test
    @Transactional
    public void createRentalOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rentalOrderRepository.findAll().size();

        // Create the RentalOrder with an existing ID
        rentalOrder.setId(1L);
        RentalOrderDTO rentalOrderDTO = rentalOrderMapper.toDto(rentalOrder);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRentalOrderMockMvc.perform(post("/api/rental-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentalOrderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RentalOrder in the database
        List<RentalOrder> rentalOrderList = rentalOrderRepository.findAll();
        assertThat(rentalOrderList).hasSize(databaseSizeBeforeCreate);

        // Validate the RentalOrder in Elasticsearch
        verify(mockRentalOrderSearchRepository, times(0)).save(rentalOrder);
    }


    @Test
    @Transactional
    public void checkTotalAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentalOrderRepository.findAll().size();
        // set the field null
        rentalOrder.setTotalAmount(null);

        // Create the RentalOrder, which fails.
        RentalOrderDTO rentalOrderDTO = rentalOrderMapper.toDto(rentalOrder);


        restRentalOrderMockMvc.perform(post("/api/rental-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentalOrderDTO)))
            .andExpect(status().isBadRequest());

        List<RentalOrder> rentalOrderList = rentalOrderRepository.findAll();
        assertThat(rentalOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCurencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentalOrderRepository.findAll().size();
        // set the field null
        rentalOrder.setCurrency(null);

        // Create the RentalOrder, which fails.
        RentalOrderDTO rentalOrderDTO = rentalOrderMapper.toDto(rentalOrder);


        restRentalOrderMockMvc.perform(post("/api/rental-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentalOrderDTO)))
            .andExpect(status().isBadRequest());

        List<RentalOrder> rentalOrderList = rentalOrderRepository.findAll();
        assertThat(rentalOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentalOrderRepository.findAll().size();
        // set the field null
        rentalOrder.setStatus(null);

        // Create the RentalOrder, which fails.
        RentalOrderDTO rentalOrderDTO = rentalOrderMapper.toDto(rentalOrder);


        restRentalOrderMockMvc.perform(post("/api/rental-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(rentalOrderDTO)))
            .andExpect(status().isBadRequest());

        List<RentalOrder> rentalOrderList = rentalOrderRepository.findAll();
        assertThat(rentalOrderList).hasSize(databaseSizeBeforeTest);
    }



    @Test
    @Transactional
    public void getAllRentalOrders() throws Exception {
        // Initialize the database
        rentalOrderRepository.saveAndFlush(rentalOrder);

        // Get all the rentalOrderList
        restRentalOrderMockMvc.perform(get("/api/rental-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rentalOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].lateChargedAmount").value(hasItem(DEFAULT_LATE_CHARGED_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURENCY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastUpdatedAt").value(hasItem(DEFAULT_LAST_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }
    
    @Test
    @Transactional
    public void getRentalOrder() throws Exception {
        // Initialize the database
        rentalOrderRepository.saveAndFlush(rentalOrder);

        // Get the rentalOrder
        restRentalOrderMockMvc.perform(get("/api/rental-orders/{id}", rentalOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rentalOrder.getId().intValue()))
            .andExpect(jsonPath("$.lateChargedAmount").value(DEFAULT_LATE_CHARGED_AMOUNT.intValue()))
            .andExpect(jsonPath("$.totalAmount").value(DEFAULT_TOTAL_AMOUNT.intValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURENCY.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.lastUpdatedAt").value(DEFAULT_LAST_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }
    @Test
    @Transactional
    public void getNonExistingRentalOrder() throws Exception {
        // Get the rentalOrder
        restRentalOrderMockMvc.perform(get("/api/rental-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }


    @Test
    @Transactional
    public void deleteRentalOrder() throws Exception {
        // Initialize the database
        rentalOrderRepository.saveAndFlush(rentalOrder);

        int databaseSizeBeforeDelete = rentalOrderRepository.findAll().size();

        // Delete the rentalOrder
        restRentalOrderMockMvc.perform(delete("/api/rental-orders/{id}", rentalOrder.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RentalOrder> rentalOrderList = rentalOrderRepository.findAll();
        assertThat(rentalOrderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RentalOrder in Elasticsearch
        verify(mockRentalOrderSearchRepository, times(1)).deleteById(rentalOrder.getId());
    }

    @Test
    @Transactional
    public void searchRentalOrder() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        rentalOrderRepository.saveAndFlush(rentalOrder);
        when(mockRentalOrderSearchRepository.search(queryStringQuery("id:" + rentalOrder.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(rentalOrder), PageRequest.of(0, 1), 1));

        // Search the rentalOrder
        restRentalOrderMockMvc.perform(get("/api/_search/rental-orders?query=id:" + rentalOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rentalOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].lateChargedAmount").value(hasItem(DEFAULT_LATE_CHARGED_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURENCY.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].lastUpdatedAt").value(hasItem(DEFAULT_LAST_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }
}
