package com.roundrobine.movie.rentals.repository.search;

import com.roundrobine.movie.rentals.domain.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Movie} entity.
 */
public interface MovieSearchRepository extends ElasticsearchRepository<Movie, Long> {
}
