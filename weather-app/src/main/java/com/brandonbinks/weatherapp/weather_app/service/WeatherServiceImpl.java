package com.brandonbinks.weatherapp.weather_app.service;


import com.brandonbinks.weatherapp.weather_app.client.OpenWeatherMapClient;
import com.brandonbinks.weatherapp.weather_app.exception.MissingFieldException;
import com.brandonbinks.weatherapp.weather_app.model.WeatherEntity;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import com.brandonbinks.weatherapp.weather_app.repository.WeatherRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    
    @Autowired
    private OpenWeatherMapClient openWeatherMapClient;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private ApiService apiService;

    @Value("${openweathermap.api.key}")
    private String weatherApiKey;


    @Override
    public WeatherResponse getWeather(String city, String country, String apiKey){
        apiService.validateApiKey(apiKey);
        apiService.enforceRateLimit(apiKey);
        validateFields(city, country);

        String cityCountry = city + "," + country;
        WeatherResponse response = openWeatherMapClient.getWeather(cityCountry, weatherApiKey);

        if (response != null && response.getWeather() != null && !response.getWeather().isEmpty()){
            updateWeatherData(city, country, response);
        }

        return checkDatabaseForWeather(city, country);
    }

    @Override
    public void validateFields(String city, String country) {
        if (city == null || city.isEmpty()) {
            throw new MissingFieldException("City field is missing.");
        }

        if (country == null || country.isEmpty()) {
            throw new MissingFieldException("Country field is missing.");
        }
    }

    @Override
    public void updateWeatherData(String city, String country, WeatherResponse response) {
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findByCityAndCountry(city, country);

        if (weatherEntityOptional.isPresent()) {
            WeatherEntity weatherEntity = weatherEntityOptional.get();
            String newDescription = response.getWeather().getFirst().getDescription();

            if (!weatherEntity.getDescription().equals(newDescription)) {
                weatherEntity.setDescription(newDescription);
                weatherRepository.save(weatherEntity);
            }
        } else {
            WeatherEntity weatherEntity = new WeatherEntity(city, country, response.getWeather().getFirst().getDescription());
            weatherRepository.save(weatherEntity);
        }
    }

    @Override
    public WeatherResponse checkDatabaseForWeather(String city, String country) {
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findByCityAndCountry(city, country);

        if (weatherEntityOptional.isPresent()) {
            WeatherEntity weatherEntity = weatherEntityOptional.get();
            WeatherResponse response = new WeatherResponse();
            WeatherResponse.Weather weather = new WeatherResponse.Weather();
            weather.setDescription(weatherEntity.getDescription());
            response.setWeather(List.of(weather));
            return response;
        }

        return null;
    }

}
