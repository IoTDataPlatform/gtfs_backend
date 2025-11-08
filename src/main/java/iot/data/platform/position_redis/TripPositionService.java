package iot.data.platform.position_redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class TripPositionService {

    private final HashOperations<String, String, String> h;
    private final String tripPrefix;
    private final long recencySecondsDefault;

    public TripPositionService(
            StringRedisTemplate redis,
            @Value("${vehpos.keys.trip-prefix}") String tripPrefix,
            @Value("${vehpos.recency-seconds:120}") long recencySeconds
    ) {
        this.h = redis.opsForHash();
        this.tripPrefix = tripPrefix;
        this.recencySecondsDefault = recencySeconds;
    }

    public Optional<TripPositionDto> findFreshByTripId(String tripId, Long recencyOverride) {
        long recencySeconds = (recencyOverride != null) ? recencyOverride : recencySecondsDefault;
        String key = tripPrefix + tripId;

        Map<String, String> m = h.entries(key);
        if (m == null || m.isEmpty()) return Optional.empty();

        TripPositionDto dto = fromHash(m, tripId);
        if (dto == null || dto.tripId == null || dto.tripId.isBlank()) return Optional.empty();

        boolean fresh = isFresh(dto, recencySeconds);
        if (!fresh) return Optional.empty();

        dto.inTransit = true;
        return Optional.of(dto);
    }

    private TripPositionDto fromHash(Map<String, String> m, String fallbackTripId) {
        TripPositionDto d = new TripPositionDto();
        d.tripId     = firstNonBlank(m.get("tripId"), m.get("trip_id"), m.get("trip"));
        if (d.tripId == null || d.tripId.isBlank()) d.tripId = fallbackTripId;

        d.vehicleId  = firstNonBlank(m.get("vehicleId"), m.get("vehicle_id"), m.get("id"));
        d.routeId    = firstNonBlank(m.get("routeId"), m.get("route_id"));
        d.status     = firstNonBlank(m.get("status"), m.get("current_status"), m.get("state"));
        d.lat        = parseD(firstNonBlank(m.get("lat"), m.get("latitude")));
        d.lon        = parseD(firstNonBlank(m.get("lon"), m.get("lng"), m.get("longitude")));
        d.speed      = parseD(m.get("speed"));
        d.bearing    = parseD(m.get("bearing"));

        d.lastUpdated= parseInstant(firstNonBlank(
                m.get("ts"), m.get("timestamp"), m.get("header_ts"), m.get("time"),
                m.get("updated_at"), m.get("last_update"), m.get("last_seen"), m.get("lastSeen")
        ));
        return d;
    }

    private String firstNonBlank(String... v) {
        if (v == null) return null;
        for (String s : v) if (s != null && !s.isBlank()) return s;
        return null;
    }
    private Double parseD(String s) {
        try { return (s == null || s.isBlank()) ? null : Double.valueOf(s); }
        catch (Exception e) { return null; }
    }
    private Instant parseInstant(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            String t = s.trim();
            if (t.contains(".")) {
                double sec = Double.parseDouble(t);
                long ms = (long) Math.round(sec * 1000.0);
                return Instant.ofEpochMilli(ms);
            }
            long v = Long.parseLong(t);
            return (t.length() <= 10) ? Instant.ofEpochSecond(v) : Instant.ofEpochMilli(v);
        } catch (Exception e) {
            try { return Instant.parse(s); } catch (Exception ignore) { return null; }
        }
    }
    private boolean isFresh(TripPositionDto d, long recencySeconds) {
        return d.lastUpdated != null && Instant.now().minusSeconds(recencySeconds).isBefore(d.lastUpdated);
    }
}
