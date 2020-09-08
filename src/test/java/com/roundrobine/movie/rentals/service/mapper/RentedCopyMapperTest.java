package com.roundrobine.movie.rentals.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RentedCopyMapperTest {

    private RentedCopyMapper rentedCopyMapper;

    @BeforeEach
    public void setUp() {
        rentedCopyMapper = new RentedCopyMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(rentedCopyMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(rentedCopyMapper.fromId(null)).isNull();
    }
}
