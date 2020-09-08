package com.roundrobine.movie.rentals.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

import com.roundrobine.movie.rentals.domain.User;
import com.roundrobine.movie.rentals.domain.enumeration.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.roundrobine.movie.rentals.domain.Customer} entity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDTO implements Serializable {
    
    private Long id;
    private Long bonusPoints;
    private BigDecimal creditAmount;
    private Currency currency;
    private User user;

}
