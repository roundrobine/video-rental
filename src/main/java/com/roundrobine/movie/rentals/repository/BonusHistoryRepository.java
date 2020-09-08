package com.roundrobine.movie.rentals.repository;

import com.roundrobine.movie.rentals.domain.BonusHistory;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the BonusHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BonusHistoryRepository extends JpaRepository<BonusHistory, Long> {
}
