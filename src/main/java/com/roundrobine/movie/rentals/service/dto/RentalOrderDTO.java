package com.roundrobine.movie.rentals.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.roundrobine.movie.rentals.domain.Customer;
import com.roundrobine.movie.rentals.domain.RentedCopy;
import com.roundrobine.movie.rentals.domain.enumeration.Currency;
import com.roundrobine.movie.rentals.domain.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.roundrobine.movie.rentals.domain.RentalOrder} entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RentalOrderDTO implements Serializable {
    
    private Long id;

    private BigDecimal lateChargedAmount;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Currency currency;

    @NotNull
    private OrderStatus status;

    @NotNull
    private Instant lastUpdatedAt = Instant.now();

    @NotNull
    private Instant createdAt = Instant.now();

    @NotNull
    private Customer customer;

    @NotNull
    private Set<RentedCopy> rentedCopies = new HashSet<>();


}
