package com.roundrobine.movie.rentals.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRentedMovieDTO {

    @NotNull
    private List<Long> movieInventoryIds;
}
