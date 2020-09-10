package com.roundrobine.movie.rentals.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.roundrobine.movie.rentals.domain.enumeration.Currency;

import com.roundrobine.movie.rentals.domain.enumeration.OrderStatus;

/**
 * A RentalOrder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rental_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "rentalorder")
public class RentalOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "late_charged_amount", precision = 21, scale = 2)
    @Builder.Default
    private BigDecimal lateChargedAmount = new BigDecimal("0");;

    @NotNull
    @Column(name = "total_amount", precision = 21, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = new BigDecimal("0");

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "curency", nullable = false)
    @EqualsAndHashCode.Include
    private Currency currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    @EqualsAndHashCode.Include
    private OrderStatus status = OrderStatus.NEW;

    @NotNull
    @Column(name = "last_updated_at", nullable = false)
    @Builder.Default
    private Instant lastUpdatedAt = Instant.now();

    @NotNull
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "order")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ToString.Exclude
    private Set<RentedCopy> rentedCopies = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "rentalOrders", allowSetters = true)
    @EqualsAndHashCode.Include
    private Customer customer;

}
