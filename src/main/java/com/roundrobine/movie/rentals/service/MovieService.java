package com.roundrobine.movie.rentals.service;

import com.roundrobine.movie.rentals.domain.Movie;
import com.roundrobine.movie.rentals.repository.MovieRepository;
import com.roundrobine.movie.rentals.repository.search.MovieSearchRepository;
import com.roundrobine.movie.rentals.service.dto.MovieDTO;
import com.roundrobine.movie.rentals.service.mapper.MovieMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Movie}.
 */
@Service
@Transactional
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;

    private final MovieMapper movieMapper;

    private final MovieSearchRepository movieSearchRepository;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper,
                        MovieSearchRepository movieSearchRepository) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.movieSearchRepository = movieSearchRepository;
    }

    /**
     * Save a movie.
     *
     * @param movieDTO the entity to save.
     * @return the persisted entity.
     */
    public MovieDTO save(MovieDTO movieDTO) {
        log.debug("Request to save Movie : {}", movieDTO);
        Movie movie = movieMapper.toEntity(movieDTO);
        movie = movieRepository.save(movie);
        MovieDTO result = movieMapper.toDto(movie);
        movieSearchRepository.save(movie);
        return result;
    }

    /**
     * Get all the movies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MovieDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Movies");
        return movieRepository.findAll(pageable)
            .map(movieMapper::toDto);
    }


    /**
     * Get one movie by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MovieDTO> findOne(Long id) {
        log.debug("Request to get Movie : {}", id);
        return movieRepository.findById(id)
            .map(movieMapper::toDto);
    }

    /**
     * Delete the movie by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Movie : {}", id);
        movieRepository.deleteById(id);
        movieSearchRepository.deleteById(id);
    }

    /**
     * Search for the movie corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MovieDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Movies for query {}", query);

        Map<String, Float> defaultFields = new HashMap<>();
        defaultFields.put("title", 5.0f);
        defaultFields.put("description", 2.0f);

        QueryBuilder queryObject = QueryBuilders.boolQuery()
            .must(QueryBuilders.queryStringQuery(query)
                .fields(defaultFields)
                .fuzziness(Fuzziness.AUTO)
                .phraseSlop(5));

        return movieSearchRepository.search(queryObject, pageable)
            .map(movieMapper::toDto);
    }
}
