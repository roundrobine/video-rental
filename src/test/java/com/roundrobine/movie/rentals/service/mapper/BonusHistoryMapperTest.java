package com.roundrobine.movie.rentals.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BonusHistoryMapperTest {

    private BonusHistoryMapper bonusHistoryMapper;

    @BeforeEach
    public void setUp() {
        bonusHistoryMapper = new BonusHistoryMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(bonusHistoryMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(bonusHistoryMapper.fromId(null)).isNull();
    }
}
