package com.roundrobine.movie.rentals.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.roundrobine.movie.rentals.web.rest.TestUtil;

public class BonusHistoryTest {

    @Test
    public void equalsVerifier() throws Exception {
        BonusHistory bonusHistory1 = new BonusHistory();
        bonusHistory1.setId(1L);
        BonusHistory bonusHistory2 = new BonusHistory();
        bonusHistory2.setId(bonusHistory1.getId());
        assertThat(bonusHistory1.getId()).isEqualTo(bonusHistory2.getId());
        bonusHistory2.setId(2L);
        assertThat(bonusHistory1.getId()).isNotEqualTo(bonusHistory2.getId());
        bonusHistory1.setId(null);
        assertThat(bonusHistory1.getId()).isNotEqualTo(bonusHistory2.getId());
    }
}
