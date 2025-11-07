# GET endpoints

GET http://localhost:8080/api/stops/in-rect?topLeftLat={lat}&topLeftLon={lon}&bottomRightLat={lat}&bottomRightLon={lon}  
— Возвращает все остановки внутри прямоугольника (координаты и названия).

```json
[
  {
    "id": "9021001041439000",
    "name": "Björkås",
    "lat": 59.311648,
    "lon": 18.702539
  },
  {
    "id": "9022001041439002",
    "name": "Björkås",
    "lat": 59.311703,
    "lon": 18.701839
  }
]
```

GET http://localhost:8080/api/stops/{stopId}/routes  
— Возвращает список всех маршрутов, проходящих через указанную остановку.

```json
[
  {
    "routeId": "9011001000100000",
    "shortName": "1",
    "longName": null,
    "routeType": 700
  },
  {
    "routeId": "9011001007600000",
    "shortName": "76",
    "longName": null,
    "routeType": 700
  }
]
```

GET http://localhost:8080/api/stops/{stopId}/routes/{routeId}/times?date=YYYY-MM-DD  
— Возвращает все времена (HH:MM), когда указанный маршрут останавливается на этой остановке в указанную дату.

```json
{
  "stopId": "9022001010028003",
  "routeId": "9011001000100000",
  "date": "2025-10-19",
  "shortName": "1",
  "longName": null,
  "routeType": 700,
  "times": [
    "06:05",
    "06:25",
    "06:45",
    "07:05",
    "07:25"
  ]
}
```

GET http://localhost:8080/api/routes/{routeId}/geometry  
— Возвращает геометрию маршрута (линия) и список его остановок с координатами.

```json
{
  "routeId": "9011001000100000",
  "stops": [
    {
      "id": "9022001010455002",
      "name": "Broparken",
      "lat": 59.324126,
      "lon": 17.993926
    },
    {
      "id": "9022001010455003",
      "name": "Broparken",
      "lat": 59.324088,
      "lon": 17.99359
    }
  ],
  "shapes": [
    {
      "shapeId": "1014010000482329256",
      "points": [
        {
          "lat": 59.341873,
          "lon": 18.118316
        }
      ]
    },
    {
      "shapeId": "1014010000560262054",
      "points": [
        {
          "lat": 59.320283,
          "lon": 17.988283
        }
      ]
    }
  ]
}
  
```

GET http://localhost:8080/api/trips/{tripId}/shape
— Геометрия рейса: список точек (lat, lon, sequence).

```json
{
  "tripId": "14010000635971836",
  "routeId": "9011001000100000",
  "shapeId": "1014010000482329256",
  "points": [
    {
      "lat": 59.341873,
      "lon": 18.118316,
      "sequence": 1
    }
  ]
}
```

GET http://localhost:8080/api/trips/{tripId}/stops
— Все остановки рейса с координатами, порядком следования, временем прибытия и отправки.

```json
{
  "tripId": "14010000635971836",
  "routeId": "9011001000100000",
  "stops": [
    {
      "stopId": "9022001010028003",
      "stopName": "Frihamnen",
      "lat": 59.341873,
      "lon": 18.118316,
      "sequence": 1,
      "arrivalTime": "22:00:00",
      "departureTime": "22:00:00"
    }
  ]
}
```