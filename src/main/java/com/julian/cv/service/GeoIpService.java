package com.julian.cv.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.julian.cv.model.GeoIpData;

@Service
public class GeoIpService {

    private final RestTemplate restTemplate = new RestTemplate();

    public GeoIpData getGeoData(String ip) {

        try {
            String url = "https://ipinfo.io/" + ip + "/json";

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            Map body = response.getBody();

            if (body == null) return new GeoIpData(null, null, null);

            String city = (String) body.get("city");
            String region = (String) body.get("region");
            String country = (String) body.get("country");

            return new GeoIpData(country, city, region);

        } catch (Exception e) {
            return new GeoIpData(null, null, null);
        }
    }
}