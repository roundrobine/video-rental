package com.roundrobine.movie.rentals.repository;

import com.roundrobine.movie.rentals.domain.RentalOrder;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the RentalOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentalOrderRepository extends JpaRepository<RentalOrder, Long> {
}
