package com.roundrobine.movie.rentals.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;

/**
 * A RentedCopy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "rented_copy")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rentedcopy")
public class RentedCopy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "rent_date", nullable = false)
    private Instant rentDate = Instant.now();

    @NotNull
    @Column(name = "planned_rent_duration", nullable = false)
    private Integer plannedRentDuration;

    @Column(name = "extra_charged_days")
    private Integer extraChargedDays;

    @Column(name = "return_date")
    private Instant returnDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "rentedCopies", allowSetters = true)
    private MovieInventory movieInventory;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "rentedCopies", allowSetters = true)
    private RentalOrder order;

}
