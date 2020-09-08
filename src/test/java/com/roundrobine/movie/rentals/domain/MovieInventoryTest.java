package com.roundrobine.movie.rentals.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class MovieInventoryTest {

    @Test
    public void equalsVerifier() throws Exception {
        MovieInventory movieInventory1 = new MovieInventory();
        movieInventory1.setId(1L);
        MovieInventory movieInventory2 = new MovieInventory();
        movieInventory2.setId(movieInventory1.getId());
        assertThat(movieInventory1.getId()).isEqualTo(movieInventory2.getId());
        movieInventory2.setId(2L);
        assertThat(movieInventory1.getId()).isNotEqualTo(movieInventory2.getId());
        movieInventory1.setId(null);
        assertThat(movieInventory1.getId()).isNotEqualTo(movieInventory2.getId());
    }
}
