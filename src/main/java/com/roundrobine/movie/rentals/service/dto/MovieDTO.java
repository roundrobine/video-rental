package com.roundrobine.movie.rentals.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import com.roundrobine.movie.rentals.domain.enumeration.MovieType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO for the {@link com.roundrobine.movie.rentals.domain.Movie} entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    private MovieType type;

    private String posterUrl;


}
