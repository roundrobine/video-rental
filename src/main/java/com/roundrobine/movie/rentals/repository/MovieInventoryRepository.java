package com.roundrobine.movie.rentals.repository;

import com.roundrobine.movie.rentals.domain.MovieInventory;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the MovieInventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovieInventoryRepository extends JpaRepository<MovieInventory, Long> {
}
