package com.roundrobine.movie.rentals.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RentalOrderMapperTest {

    private RentalOrderMapper rentalOrderMapper;

    @BeforeEach
    public void setUp() {
        rentalOrderMapper = new RentalOrderMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(rentalOrderMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(rentalOrderMapper.fromId(null)).isNull();
    }
}
