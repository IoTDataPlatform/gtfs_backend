package iot.data.platform.trips;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TripsDao {
    private final JdbcTemplate jdbc;

    public TripsDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public TripStopsResponse getTripStops(String tripId) {
        String routeId = null;
        try {
            routeId = jdbc.queryForObject(
                    "SELECT route_id FROM trips WHERE trip_id = ?::text",
                    String.class, tripId);
        } catch (EmptyResultDataAccessException ignored) {}

        final String sql = """
            SELECT
              st.stop_id,
              s.stop_name,
              ST_Y(s.stop_loc::geometry) AS lat,
              ST_X(s.stop_loc::geometry) AS lon,
              st.stop_sequence,
              TO_CHAR(st.arrival_time::time,   'HH24:MI:SS') AS arr,
              TO_CHAR(st.departure_time::time, 'HH24:MI:SS') AS dep
            FROM stop_times st
            JOIN stops s USING (stop_id)
            WHERE st.trip_id = ?::text
            ORDER BY st.stop_sequence
            """;

        List<TripStopDto> stops = jdbc.query(sql, (rs, n) ->
                new TripStopDto(
                        rs.getString("stop_id"),
                        rs.getString("stop_name"),
                        rs.getDouble("lat"),
                        rs.getDouble("lon"),
                        rs.getInt("stop_sequence"),
                        rs.getString("arr"),
                        rs.getString("dep")
                ), tripId);

        return new TripStopsResponse(tripId, routeId, List.copyOf(stops));
    }


    public TripShapeResponse getTripShape(String tripId) {
        String routeId = null;
        String shapeId = null;
        try {
            var row = jdbc.queryForMap("""
                SELECT route_id, shape_id
                FROM trips
                WHERE trip_id = ?::text
                """, tripId);
            routeId = (String) row.get("route_id");
            shapeId = (String) row.get("shape_id");
        } catch (Exception ignored) {}

        List<LatLon> points = new ArrayList<>();
        if (shapeId != null && !shapeId.isBlank()) {
            final String sqlShapes = """
                SELECT
                  ST_Y(s.shape_pt_loc::geometry) AS lat,
                  ST_X(s.shape_pt_loc::geometry) AS lon,
                  s.shape_pt_sequence            AS seq
                FROM shapes s
                WHERE s.shape_id = ?::text
                ORDER BY s.shape_pt_sequence
                """;
            points = jdbc.query(sqlShapes, (rs, n) ->
                    new LatLon(rs.getDouble("lat"), rs.getDouble("lon"), rs.getInt("seq")), shapeId);
        }

        return new TripShapeResponse(tripId, routeId, shapeId, List.copyOf(points));
    }
}
