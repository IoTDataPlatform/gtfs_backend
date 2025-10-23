package com.example.gtfs.routes;

import com.example.gtfs.stops.StopDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RoutesController {

    private final RoutesDao dao;

    public RoutesController(RoutesDao dao) {
        this.dao = dao;
    }

    @GetMapping("/{routeId}/geometry")
    public RouteGeometryDto geometry(@PathVariable String routeId) {
        List<StopDto> stops = dao.findStopsByRoute(routeId);

        Map<String, List<LatLon>> shapesById = dao.findShapesByRoute(routeId);
        List<ShapeDto> shapes = new ArrayList<>(shapesById.size());
        for (var e : shapesById.entrySet()) {
            shapes.add(new ShapeDto(e.getKey(), e.getValue()));
        }

        return new RouteGeometryDto(routeId, stops, shapes);
    }
}
