package com.roundrobine.movie.rentals.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class RentalOrderTest {

    @Test
    public void equalsVerifier() throws Exception {
        RentalOrder rentalOrder1 = new RentalOrder();
        rentalOrder1.setId(1L);
        RentalOrder rentalOrder2 = new RentalOrder();
        rentalOrder2.setId(rentalOrder1.getId());
        assertThat(rentalOrder1.getId()).isEqualTo(rentalOrder2.getId());
        rentalOrder2.setId(2L);
        assertThat(rentalOrder1.getId()).isNotEqualTo(rentalOrder2.getId());
        rentalOrder1.setId(null);
        assertThat(rentalOrder1.getId()).isNotEqualTo(rentalOrder2.getId());
    }
}
