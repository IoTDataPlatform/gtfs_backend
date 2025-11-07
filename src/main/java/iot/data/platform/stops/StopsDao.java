package iot.data.platform.stops;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StopsDao {
    private static final Logger log = LoggerFactory.getLogger(StopsDao.class);
    private final JdbcTemplate jdbc;

    public StopsDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StopDto> findInRect(double north, double south, double west, double east) {
        final boolean crossesAntiMeridian = west > east;
        final String base = """
            SELECT
                stop_id   AS id,
                stop_name AS name,
                ST_Y((stop_loc::geometry))::double precision AS lat,
                ST_X((stop_loc::geometry))::double precision AS lon
            FROM stops
            WHERE (ST_Y((stop_loc::geometry))::double precision) BETWEEN ? AND ?
              AND (
                %s
              )
            ORDER BY stop_name
            """;

        final String lonCond = crossesAntiMeridian
                ? " ( (ST_X((stop_loc::geometry))::double precision) >= ? OR (ST_X((stop_loc::geometry))::double precision) <= ? ) "
                : " ( (ST_X((stop_loc::geometry))::double precision) BETWEEN ? AND ? ) ";

        final String sql = base.formatted(lonCond);

        Object[] params = new Object[]{south, north, west, east};

        try {
            return jdbc.query(sql, params, StopsDao::mapRow);
        } catch (Exception e) {
            log.error("DB error in findInRect(north={}, south={}, west={}, east={}): {}", north, south, west, east, e.toString(), e);
            throw e;
        }
    }

    private static StopDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StopDto(
                rs.getString("id"),
                rs.getString("name"),
                rs.getDouble("lat"),
                rs.getDouble("lon")
        );
    }
}
