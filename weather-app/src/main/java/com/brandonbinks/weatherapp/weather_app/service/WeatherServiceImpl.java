package com.brandonbinks.weatherapp.weather_app.service;


import com.brandonbinks.weatherapp.weather_app.client.OpenWeatherMapClient;
import com.brandonbinks.weatherapp.weather_app.exception.ApiKeyLimitExceededException;
import com.brandonbinks.weatherapp.weather_app.exception.InvalidApiKeyException;
import com.brandonbinks.weatherapp.weather_app.exception.MissingFieldException;
import com.brandonbinks.weatherapp.weather_app.model.WeatherEntity;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import com.brandonbinks.weatherapp.weather_app.repository.WeatherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WeatherServiceImpl implements WeatherService {
    
    @Autowired
    private OpenWeatherMapClient openWeatherMapClient;

    @Autowired
    private WeatherRepository weatherRepository;

    private final Clock clock;

    private static final int RATE_LIMIT = 5;
    private static final ConcurrentHashMap<String, AtomicInteger> apiKeyUsage = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LocalDateTime> apiKeyTimestamp = new ConcurrentHashMap<>();

    private static final String weatherApiKey = "9894e3062fefc500c28ea35af4375279";

    private static final String[] VALID_API_KEYS = {
        "API_KEY_1", "API_KEY_2", "API_KEY_3", "API_KEY_4", "API_KEY_5"
    };

    public WeatherServiceImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public WeatherResponse getWeather(String city, String country, String apiKey){
        if(!isValidApiKey(apiKey)){
            throw new InvalidApiKeyException("Invalid API Key.");
        }

        if (city == null || city.isEmpty()) {
            throw new MissingFieldException("City field is missing.");
        } 
        
        if (country == null || country.isEmpty()) {
            throw new MissingFieldException("Country field is missing.");
        }

        String cityCountry = city + "," + country;
        enforceRateLimit(apiKey);

        WeatherResponse response = openWeatherMapClient.getWeather(cityCountry, weatherApiKey);
        if (response != null && response.getWeather() != null && !response.getWeather().isEmpty()){
            WeatherEntity weatherEntity = new WeatherEntity(city, country, response.getWeather().get(0).getDescription());
            weatherRepository.save(weatherEntity);
        }

        return response;
    }

    private boolean isValidApiKey(String apiKey){
        for (String validApiKey : VALID_API_KEYS){
            if (validApiKey.equals(apiKey)){
                return true;
            }
        }
        return false;
    }

    private void enforceRateLimit(String apiKey) {
        LocalDateTime now = LocalDateTime.now(clock);
        apiKeyUsage.putIfAbsent(apiKey, new AtomicInteger(0));
        apiKeyTimestamp.putIfAbsent(apiKey, now);

        if (apiKeyUsage.get(apiKey).get() >= RATE_LIMIT &&
            apiKeyTimestamp.get(apiKey).isAfter(now.minusHours(1))){
                throw new ApiKeyLimitExceededException("Hourly limit exceeded.");
            }
        
        if (apiKeyTimestamp.get(apiKey).isBefore(now.minusHours(1))){
            apiKeyUsage.get(apiKey).set(0);
            apiKeyTimestamp.put(apiKey, now);
        }

        apiKeyUsage.get(apiKey).incrementAndGet();
    }
}
