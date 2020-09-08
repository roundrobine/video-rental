package com.roundrobine.movie.rentals.repository.search;

import com.roundrobine.movie.rentals.domain.RentalOrder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link RentalOrder} entity.
 */
public interface RentalOrderSearchRepository extends ElasticsearchRepository<RentalOrder, Long> {
}
