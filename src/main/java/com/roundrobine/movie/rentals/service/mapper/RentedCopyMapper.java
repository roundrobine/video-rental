package com.roundrobine.movie.rentals.service.mapper;


import com.roundrobine.movie.rentals.domain.*;
import com.roundrobine.movie.rentals.service.dto.RentedCopyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link RentedCopy} and its DTO {@link RentedCopyDTO}.
 */
@Mapper(componentModel = "spring", uses = {MovieInventoryMapper.class, RentalOrderMapper.class})
public interface RentedCopyMapper extends EntityMapper<RentedCopyDTO, RentedCopy> {

    RentedCopyDTO toDto(RentedCopy rentedCopy);
    RentedCopy toEntity(RentedCopyDTO rentedCopyDTO);

    default RentedCopy fromId(Long id) {
        if (id == null) {
            return null;
        }
        RentedCopy rentedCopy = new RentedCopy();
        rentedCopy.setId(id);
        return rentedCopy;
    }
}
