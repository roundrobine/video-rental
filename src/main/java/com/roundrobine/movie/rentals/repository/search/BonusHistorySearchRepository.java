package com.roundrobine.movie.rentals.repository.search;

import com.roundrobine.movie.rentals.domain.BonusHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link BonusHistory} entity.
 */
public interface BonusHistorySearchRepository extends ElasticsearchRepository<BonusHistory, Long> {
}
