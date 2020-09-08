package com.roundrobine.movie.rentals.service.mapper;


import com.roundrobine.movie.rentals.domain.*;
import com.roundrobine.movie.rentals.service.dto.BonusHistoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link BonusHistory} and its DTO {@link BonusHistoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {CustomerMapper.class, RentalOrderMapper.class})
public interface BonusHistoryMapper extends EntityMapper<BonusHistoryDTO, BonusHistory> {

    BonusHistoryDTO toDto(BonusHistory bonusHistory);
    BonusHistory toEntity(BonusHistoryDTO bonusHistoryDTO);

    default BonusHistory fromId(Long id) {
        if (id == null) {
            return null;
        }
        BonusHistory bonusHistory = new BonusHistory();
        bonusHistory.setId(id);
        return bonusHistory;
    }
}
