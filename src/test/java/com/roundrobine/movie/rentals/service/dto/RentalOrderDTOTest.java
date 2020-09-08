package com.roundrobine.movie.rentals.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class RentalOrderDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        RentalOrderDTO rentalOrderDTO1 = new RentalOrderDTO();
        rentalOrderDTO1.setId(1L);
        RentalOrderDTO rentalOrderDTO2 = new RentalOrderDTO();
        assertThat(rentalOrderDTO1.getId()).isNotEqualTo(rentalOrderDTO2.getId());
        rentalOrderDTO2.setId(rentalOrderDTO1.getId());
        assertThat(rentalOrderDTO1.getId()).isEqualTo(rentalOrderDTO2.getId());
        rentalOrderDTO2.setId(2L);
        assertThat(rentalOrderDTO1.getId()).isNotEqualTo(rentalOrderDTO2.getId());
        rentalOrderDTO1.setId(null);
        assertThat(rentalOrderDTO1.getId()).isNotEqualTo(rentalOrderDTO2.getId());
    }
}
