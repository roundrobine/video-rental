package com.roundrobine.movie.rentals.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.roundrobine.movie.rentals.domain.Movie;
import com.roundrobine.movie.rentals.domain.enumeration.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.roundrobine.movie.rentals.domain.MovieInventory} entity.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInventoryDTO implements Serializable {
    
    private Long id;

    private Instant lastUpdatedAt;

    private Instant createdAt = Instant.now();

    private RentalStatus status = RentalStatus.AVAILABLE;

    @NotNull
    private Movie movie;

}
