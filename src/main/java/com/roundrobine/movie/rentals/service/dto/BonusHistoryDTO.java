package com.roundrobine.movie.rentals.service.dto;

import com.roundrobine.movie.rentals.domain.Customer;
import com.roundrobine.movie.rentals.domain.RentalOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link com.roundrobine.movie.rentals.domain.BonusHistory} entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BonusHistoryDTO implements Serializable {
    
    private Long id;

    @NotNull
    private Long points;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Customer customer;

    @NotNull
    private RentalOrder oder;

}
