package ca.shopify.inventorytrackerchallenge.database;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


/**
 * The type Database access object to access our in-memory H2-database.
 *
 * @author Marcin Koziel
 */
@Repository
public class DatabaseConfig {

    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
