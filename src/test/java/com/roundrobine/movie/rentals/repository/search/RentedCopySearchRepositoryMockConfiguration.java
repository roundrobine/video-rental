package com.roundrobine.movie.rentals.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link RentedCopySearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class RentedCopySearchRepositoryMockConfiguration {

    @MockBean
    private RentedCopySearchRepository mockRentedCopySearchRepository;

}
