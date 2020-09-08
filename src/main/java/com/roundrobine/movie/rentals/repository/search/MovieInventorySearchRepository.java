package com.roundrobine.movie.rentals.repository.search;

import com.roundrobine.movie.rentals.domain.MovieInventory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link MovieInventory} entity.
 */
public interface MovieInventorySearchRepository extends ElasticsearchRepository<MovieInventory, Long> {
}
