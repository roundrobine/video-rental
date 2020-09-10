package com.roundrobine.movie.rentals.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;;
import javax.validation.constraints.NotNull;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRentalOrderDTO {

    @NotNull
    Map<Long, Integer> order;

}
