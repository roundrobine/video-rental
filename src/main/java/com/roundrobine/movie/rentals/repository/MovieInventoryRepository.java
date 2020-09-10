package com.roundrobine.movie.rentals.repository;

import com.roundrobine.movie.rentals.domain.MovieInventory;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data  repository for the MovieInventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MovieInventoryRepository extends JpaRepository<MovieInventory, Long> {

    List<MovieInventory> findByIdIn(Set<Long> ids);

}
