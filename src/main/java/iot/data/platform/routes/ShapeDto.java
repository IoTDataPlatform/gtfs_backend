package iot.data.platform.routes;

import java.util.List;

public record ShapeDto(String shapeId, List<LatLon> points) {}