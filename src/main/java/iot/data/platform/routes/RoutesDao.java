package iot.data.platform.routes;

import iot.data.platform.stops.StopDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class RoutesDao {

    private final JdbcTemplate jdbc;

    public RoutesDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<StopDto> findStopsByRoute(String routeId) {
        final String sql = """
            SELECT DISTINCT
                s.stop_id   AS id,
                s.stop_name AS name,
                ST_Y((s.stop_loc::geometry))::double precision AS lat,
                ST_X((s.stop_loc::geometry))::double precision AS lon
            FROM stops s
            JOIN stop_times st ON st.stop_id = s.stop_id
            JOIN trips t       ON t.trip_id  = st.trip_id
            WHERE t.route_id = ?
            ORDER BY s.stop_name
            """;
        return jdbc.query(sql, (rs, i) -> mapStop(rs), routeId);
    }

    public Map<String, List<LatLon>> findShapesByRoute(String routeId) {
        final String sqlShapes = """
            SELECT DISTINCT t.shape_id
            FROM trips t
            WHERE t.route_id = ?
              AND t.shape_id IS NOT NULL
            """;
        List<String> shapeIds = jdbc.query(sqlShapes, (rs, i) -> rs.getString(1), routeId);
        if (shapeIds.isEmpty()) return Collections.emptyMap();

        final String sqlPts = """
            SELECT
                ST_Y((shape_pt_loc::geometry))::double precision AS lat,
                ST_X((shape_pt_loc::geometry))::double precision AS lon
            FROM shapes
            WHERE shape_id = ?
            ORDER BY shape_pt_sequence
            """;

        Map<String, List<LatLon>> out = new LinkedHashMap<>();
        for (String sid : shapeIds) {
            List<LatLon> pts = jdbc.query(sqlPts, (rs, i) -> new LatLon(
                    rs.getDouble("lat"),
                    rs.getDouble("lon")
            ), sid);
            if (!pts.isEmpty()) out.put(sid, pts);
        }
        return out;
    }

    private static StopDto mapStop(ResultSet rs) throws SQLException {
        return new StopDto(
                rs.getString("id"),
                rs.getString("name"),
                rs.getDouble("lat"),
                rs.getDouble("lon")
        );
    }
}
