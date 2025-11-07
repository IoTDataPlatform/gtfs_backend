package iot.data.platform.stoproutes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class StopRoutesDao {
    private final JdbcTemplate jdbc;

    public StopRoutesDao(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<RouteOnlyDto> listRoutesForStop(String stopId) {
        final String sql = """
            WITH target_stops AS (
              SELECT stop_id FROM stops WHERE stop_id = ?::text
              UNION
              SELECT stop_id FROM stops WHERE parent_station = ?::text
            )
            SELECT DISTINCT
              r.route_id, r.route_short_name, r.route_long_name, r.route_type
            FROM stop_times st
            JOIN target_stops ts ON ts.stop_id = st.stop_id
            JOIN trips  t ON t.trip_id  = st.trip_id
            JOIN routes r ON r.route_id = t.route_id
            ORDER BY r.route_short_name NULLS LAST, r.route_id
            """;

        return jdbc.query(sql, (rs, n) ->
                new RouteOnlyDto(
                        rs.getString("route_id"),
                        rs.getString("route_short_name"),
                        rs.getString("route_long_name"),
                        readIntFlex(rs.getObject("route_type"))
                ), stopId, stopId);
    }

    public TimesForRouteOnDateDto timesForRouteOnDate(String stopId, String routeId, LocalDate date) {
        final String truthyAvail = " IN ('available','1','t','true','TRUE','T')";

        final String sql = """
            WITH target_stops AS (
              SELECT stop_id FROM stops WHERE stop_id = ?::text
              UNION
              SELECT stop_id FROM stops WHERE parent_station = ?::text
            ),
            base AS (
              SELECT t.service_id
              FROM trips t
              JOIN calendar c ON c.service_id = t.service_id
              WHERE ?::date BETWEEN c.start_date::date AND c.end_date::date
                AND (
                  (EXTRACT(DOW FROM ?::date)=1 AND c.monday::text    %1$s) OR
                  (EXTRACT(DOW FROM ?::date)=2 AND c.tuesday::text   %1$s) OR
                  (EXTRACT(DOW FROM ?::date)=3 AND c.wednesday::text %1$s) OR
                  (EXTRACT(DOW FROM ?::date)=4 AND c.thursday::text  %1$s) OR
                  (EXTRACT(DOW FROM ?::date)=5 AND c.friday::text    %1$s) OR
                  (EXTRACT(DOW FROM ?::date)=6 AND c.saturday::text  %1$s) OR
                  (EXTRACT(DOW FROM ?::date)=0 AND c.sunday::text    %1$s)
                )
            ),
            added AS (
              SELECT t.service_id
              FROM trips t
              JOIN calendar_dates cd ON cd.service_id = t.service_id
              WHERE cd.date::date = ?::date
                AND cd.exception_type::text = 'added'
            ),
            removed AS (
              SELECT t.service_id
              FROM trips t
              JOIN calendar_dates cd ON cd.service_id = t.service_id
              WHERE cd.date::date = ?::date
                AND cd.exception_type::text = 'removed'
            ),
            active_services AS (
              SELECT service_id FROM base
              UNION
              SELECT service_id FROM added
              EXCEPT
              SELECT service_id FROM removed
            ),
            route_info AS (
              SELECT r.route_id, r.route_short_name, r.route_long_name, r.route_type
              FROM routes r
              WHERE r.route_id = ?::text
            ),
            times AS (
              SELECT DISTINCT
                   TO_CHAR(COALESCE(st.arrival_time, st.departure_time)::time, 'HH24:MI') AS time_hms,
                   EXTRACT(EPOCH FROM (COALESCE(st.arrival_time, st.departure_time)::interval))::int AS time_secs
              FROM stop_times st
              JOIN target_stops ts ON ts.stop_id = st.stop_id
              JOIN trips t ON t.trip_id = st.trip_id
              JOIN active_services a ON a.service_id = t.service_id
              WHERE t.route_id = ?::text
            )
            SELECT
              (SELECT route_short_name FROM route_info) AS short_name,
              (SELECT route_long_name  FROM route_info) AS long_name,
              (SELECT route_type       FROM route_info) AS route_type,
              time_hms
            FROM times
            ORDER BY time_secs, time_hms
            """.formatted(truthyAvail);


        final Object[] params = {
                stopId, stopId,
                date, // BETWEEN
                date, // monday
                date, // tuesday
                date, // wednesday
                date, // thursday
                date, // friday
                date, // saturday
                date, // sunday
                date, // added
                date, // removed

                routeId, // route_info
                routeId  // WHERE t.route_id = ?
        };

        final List<String> times = new ArrayList<>();
        final String[] shortName = { null };
        final String[] longName  = { null };
        final Integer[] routeType= { null };

        jdbc.query(sql, rs -> {
            if (shortName[0] == null) {
                shortName[0] = rs.getString("short_name");
                longName[0]  = rs.getString("long_name");
                routeType[0] = readIntFlex(rs.getObject("route_type"));
            }
            String t = rs.getString("time_hms");
            if (t != null && (times.isEmpty() || !times.get(times.size()-1).equals(t))) {
                times.add(t);
            }
        }, params);

        return new TimesForRouteOnDateDto(
                stopId, routeId, date.toString(),
                shortName[0], longName[0], routeType[0],
                List.copyOf(times)
        );
    }

    private static Integer readIntFlex(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(o.toString().trim()); } catch (Exception e) { return null; }
    }
}
