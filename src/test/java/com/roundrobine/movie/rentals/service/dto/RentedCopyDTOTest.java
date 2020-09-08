package com.roundrobine.movie.rentals.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class RentedCopyDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        RentedCopyDTO rentedCopyDTO1 = new RentedCopyDTO();
        rentedCopyDTO1.setId(1L);
        RentedCopyDTO rentedCopyDTO2 = new RentedCopyDTO();
        assertThat(rentedCopyDTO1.getId()).isNotEqualTo(rentedCopyDTO2.getId());
        rentedCopyDTO2.setId(rentedCopyDTO1.getId());
        assertThat(rentedCopyDTO1.getId()).isEqualTo(rentedCopyDTO2.getId());
        rentedCopyDTO2.setId(2L);
        assertThat(rentedCopyDTO1.getId()).isNotEqualTo(rentedCopyDTO2.getId());
        rentedCopyDTO1.setId(null);
        assertThat(rentedCopyDTO1.getId()).isNotEqualTo(rentedCopyDTO2.getId());
    }
}
