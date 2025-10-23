# GET endpoints

GET http://localhost:8080/api/stops/in-rect?topLeftLat={lat}&topLeftLon={lon}&bottomRightLat={lat}&bottomRightLon={lon}  
— Возвращает все остановки внутри прямоугольника (координаты и названия).

GET http://localhost:8080/api/stops/{stopId}/routes  
— Возвращает список всех маршрутов, проходящих через указанную остановку.

GET http://localhost:8080/api/stops/{stopId}/routes/{routeId}/times?date=YYYY-MM-DD  
— Возвращает все времена (HH:MM), когда указанный маршрут останавливается на этой остановке в указанную дату.

GET http://localhost:8080/api/routes/{routeId}/geometry  
— Возвращает геометрию маршрута (линия) и список его остановок с координатами.