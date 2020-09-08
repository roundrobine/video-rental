package com.roundrobine.movie.rentals.service.dto;

import com.roundrobine.movie.rentals.domain.Movie;
import com.roundrobine.movie.rentals.domain.MovieInventory;
import com.roundrobine.movie.rentals.domain.RentalOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link com.roundrobine.movie.rentals.domain.RentedCopy} entity.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RentedCopyDTO implements Serializable {
    
    private Long id;

    @NotNull
    private Instant rentDate = Instant.now();

    @NotNull
    private Integer plannedRentDuration;

    private Integer extraChargedDays;

    private Instant returnDate;

    @NotNull
    private MovieInventory movieInventory;

    @NotNull
    private RentalOrder order;

}
