package com.roundrobine.movie.rentals.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class RentedCopyTest {

    @Test
    public void equalsVerifier() throws Exception {
        RentedCopy rentedCopy1 = new RentedCopy();
        rentedCopy1.setId(1L);
        RentedCopy rentedCopy2 = new RentedCopy();
        rentedCopy2.setId(rentedCopy1.getId());
        assertThat(rentedCopy1.getId()).isEqualTo(rentedCopy2.getId());
        rentedCopy2.setId(2L);
        assertThat(rentedCopy1.getId()).isNotEqualTo(rentedCopy2.getId());
        rentedCopy1.setId(null);
        assertThat(rentedCopy1.getId()).isNotEqualTo(rentedCopy2.getId());
    }
}
