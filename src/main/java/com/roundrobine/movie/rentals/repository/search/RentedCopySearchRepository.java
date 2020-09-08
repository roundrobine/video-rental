package com.roundrobine.movie.rentals.repository.search;

import com.roundrobine.movie.rentals.domain.RentedCopy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link RentedCopy} entity.
 */
public interface RentedCopySearchRepository extends ElasticsearchRepository<RentedCopy, Long> {
}
