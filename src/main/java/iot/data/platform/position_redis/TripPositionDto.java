package iot.data.platform.position_redis;

import java.time.Instant;

public class TripPositionDto {
    public String tripId;
    public String vehicleId;
    public Double lat;
    public Double lon;
    public Double speed;
    public Double bearing;
    public String routeId;
    public String status;
    public Instant lastUpdated;
    public boolean inTransit;
}
