package com.roundrobine.movie.rentals.service.mapper;


import com.roundrobine.movie.rentals.domain.*;
import com.roundrobine.movie.rentals.service.dto.MovieInventoryDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link MovieInventory} and its DTO {@link MovieInventoryDTO}.
 */
@Mapper(componentModel = "spring", uses = {MovieMapper.class})
public interface MovieInventoryMapper extends EntityMapper<MovieInventoryDTO, MovieInventory> {

    MovieInventoryDTO toDto(MovieInventory movieInventory);
    MovieInventory toEntity(MovieInventoryDTO movieInventoryDTO);

    default MovieInventory fromId(Long id) {
        if (id == null) {
            return null;
        }
        MovieInventory movieInventory = new MovieInventory();
        movieInventory.setId(id);
        return movieInventory;
    }
}
