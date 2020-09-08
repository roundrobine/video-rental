package com.roundrobine.movie.rentals.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.math.BigDecimal;

import com.roundrobine.movie.rentals.domain.enumeration.Currency;

/**
 * A Customer.
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "bonus_points")
    private Long bonusPoints = 0l;

    @Column(name = "credit_amount")
    private BigDecimal creditAmount = new BigDecimal("1000.00");

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency = Currency.SEK;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @With
    private User user;

}
