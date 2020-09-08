package com.roundrobine.movie.rentals.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class MovieInventoryMapperTest {

    private MovieInventoryMapper movieInventoryMapper;

    @BeforeEach
    public void setUp() {
        movieInventoryMapper = new MovieInventoryMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(movieInventoryMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(movieInventoryMapper.fromId(null)).isNull();
    }
}
