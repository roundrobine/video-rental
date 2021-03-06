package com.roundrobine.movie.rentals.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class MovieInventoryDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        MovieInventoryDTO movieInventoryDTO1 = new MovieInventoryDTO();
        movieInventoryDTO1.setId(1L);
        MovieInventoryDTO movieInventoryDTO2 = new MovieInventoryDTO();
        assertThat(movieInventoryDTO1.getId()).isNotEqualTo(movieInventoryDTO2.getId());
        movieInventoryDTO2.setId(movieInventoryDTO1.getId());
        assertThat(movieInventoryDTO1.getId()).isEqualTo(movieInventoryDTO2.getId());
        movieInventoryDTO2.setId(2L);
        assertThat(movieInventoryDTO1.getId()).isNotEqualTo(movieInventoryDTO2.getId());
        movieInventoryDTO1.setId(null);
        assertThat(movieInventoryDTO1.getId()).isNotEqualTo(movieInventoryDTO2.getId());
    }
}
