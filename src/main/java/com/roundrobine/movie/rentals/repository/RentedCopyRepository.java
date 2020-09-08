package com.roundrobine.movie.rentals.repository;

import com.roundrobine.movie.rentals.domain.RentedCopy;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the RentedCopy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentedCopyRepository extends JpaRepository<RentedCopy, Long> {
}
