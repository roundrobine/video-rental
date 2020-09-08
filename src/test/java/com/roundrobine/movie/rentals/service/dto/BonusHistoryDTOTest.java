package com.roundrobine.movie.rentals.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class BonusHistoryDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        BonusHistoryDTO bonusHistoryDTO1 = new BonusHistoryDTO();
        bonusHistoryDTO1.setId(1L);
        BonusHistoryDTO bonusHistoryDTO2 = new BonusHistoryDTO();
        assertThat(bonusHistoryDTO1.getId()).isNotEqualTo(bonusHistoryDTO2.getId());
        bonusHistoryDTO2.setId(bonusHistoryDTO1.getId());
        assertThat(bonusHistoryDTO1.getId()).isEqualTo(bonusHistoryDTO2.getId());
        bonusHistoryDTO2.setId(2L);
        assertThat(bonusHistoryDTO1.getId()).isNotEqualTo(bonusHistoryDTO2.getId());
        bonusHistoryDTO1.setId(null);
        assertThat(bonusHistoryDTO1.getId()).isNotEqualTo(bonusHistoryDTO2.getId());
    }
}
