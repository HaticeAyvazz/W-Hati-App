package com.example.whatiapp;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:63342")
public class AppController {

    @GetMapping("/search")
    public ResponseEntity<Map<String,Object>> getWeather(@RequestParam String city) throws Exception {
        // Your API key from OpenWeatherMap
        String apiKey = "YOUR_API_KEY_HERE";    // TODO:Enter the actual API key you received from OpenWeather here.

        //Build the API request URL
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        //Send the HTTP request and receive the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(response.body(), Map.class);

            // Extract temperature, feels-like temperature, and humidity from the 'main' field
            Map<String, Object> main = (Map<String, Object>) jsonMap.get("main");
            double temp = Double.parseDouble(main.get("temp").toString());
            double feelsLike=Double.parseDouble(main.get("feels_like").toString());
            int humidity=Integer.parseInt(main.get("humidity").toString());

            // Extract weather description from the 'weather' array
            java.util.List<Map<String, Object>> weatherList =
                    (java.util.List<Map<String, Object>>) jsonMap.get("weather");
            String description = weatherList.get(0).get("description").toString();

            // Extract wind speed from the 'wind' field
            Map<String,Object> wind=(Map<String, Object>) jsonMap.get("wind");
            double windSpeed=Double.parseDouble(wind.get("speed").toString());

            // Extract geographical coordinates (latitude and longitude)
            Map<String,Object> coord=(Map<String,Object>)jsonMap.get("coord");
            double lat=Double.parseDouble(coord.get("lat").toString());
            double lon=Double.parseDouble(coord.get("lon").toString());


            Map<String, Object> result = new HashMap<>();
            result.put("city",city);
            result.put("temp",temp);
            result.put("feels_like",feelsLike);
            result.put("desc",description);
            result.put("wind",windSpeed);
            result.put("humidity",humidity);

            result.put("lat",lat);
            result.put("lon",lon);

            return ResponseEntity.ok(result);

        } else {
            ResponseEntity.status(404).body("Something went wrong");
        }
        return null;
    }
}
