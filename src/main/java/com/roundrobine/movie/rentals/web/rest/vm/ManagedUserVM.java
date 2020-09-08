package com.roundrobine.movie.rentals.web.rest.vm;

import com.roundrobine.movie.rentals.domain.enumeration.Currency;
import com.roundrobine.movie.rentals.service.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ManagedUserVM extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    private BigDecimal bonusPoints;


}
