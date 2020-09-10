package com.roundrobine.movie.rentals.repository;

import com.roundrobine.movie.rentals.domain.MovieInventory;
import com.roundrobine.movie.rentals.domain.RentedCopy;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data  repository for the RentedCopy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentedCopyRepository extends JpaRepository<RentedCopy, Long> {

    List<RentedCopy> findByMovieInventoryIdIn(Set<Long> ids);

}
