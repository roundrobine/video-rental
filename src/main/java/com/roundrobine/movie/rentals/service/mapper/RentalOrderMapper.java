package com.roundrobine.movie.rentals.service.mapper;


import com.roundrobine.movie.rentals.domain.*;
import com.roundrobine.movie.rentals.service.dto.RentalOrderDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link RentalOrder} and its DTO {@link RentalOrderDTO}.
 */
@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface RentalOrderMapper extends EntityMapper<RentalOrderDTO, RentalOrder> {

    RentalOrderDTO toDto(RentalOrder rentalOrder);
    RentalOrder toEntity(RentalOrderDTO rentalOrderDTO);

    default RentalOrder fromId(Long id) {
        if (id == null) {
            return null;
        }
        RentalOrder rentalOrder = new RentalOrder();
        rentalOrder.setId(id);
        return rentalOrder;
    }
}
