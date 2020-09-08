package com.roundrobine.movie.rentals.repository.search;

import com.roundrobine.movie.rentals.domain.Customer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Customer} entity.
 */
public interface CustomerSearchRepository extends ElasticsearchRepository<Customer, Long> {
}
