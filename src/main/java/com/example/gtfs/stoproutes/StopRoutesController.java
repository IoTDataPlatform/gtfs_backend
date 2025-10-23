package com.example.gtfs.stoproutes;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stops")
public class StopRoutesController {

    private final StopRoutesDao dao;

    public StopRoutesController(StopRoutesDao dao) { this.dao = dao; }

    @GetMapping("/{stopId}/routes")
    public List<RouteOnlyDto> routesByStop(@PathVariable String stopId) {
        return dao.listRoutesForStop(stopId);
    }

    @GetMapping("/{stopId}/routes/{routeId}/times")
    public TimesForRouteOnDateDto timesForRouteOnDate(
            @PathVariable String stopId,
            @PathVariable String routeId,
            @RequestParam("date") String date
    ) {
        LocalDate d = LocalDate.parse(date);
        return dao.timesForRouteOnDate(stopId, routeId, d);
    }
}
