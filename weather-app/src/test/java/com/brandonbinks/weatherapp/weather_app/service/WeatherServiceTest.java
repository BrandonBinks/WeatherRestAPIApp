package com.brandonbinks.weatherapp.weather_app.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.brandonbinks.weatherapp.weather_app.client.OpenWeatherMapClient;
import com.brandonbinks.weatherapp.weather_app.exception.ApiKeyLimitExceededException;
import com.brandonbinks.weatherapp.weather_app.exception.InvalidApiKeyException;
import com.brandonbinks.weatherapp.weather_app.exception.MissingFieldException;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse.Weather;
import com.brandonbinks.weatherapp.weather_app.repository.WeatherRepository;

@SpringBootTest
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private OpenWeatherMapClient openWeatherMapClient;

    @MockBean
    private WeatherRepository weatherRepository;

    @MockBean
    private Clock clock;

    @BeforeEach
    public void setup() {
        Instant fixedInstant = Instant.parse("2024-07-24T10:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }
    
    @Test
    public void testGetWeather(){
        WeatherResponse.Weather weather = new WeatherResponse.Weather();
        weather.setDescription("clear sky");

        WeatherResponse response = new WeatherResponse();
        response.setWeather(List.of(weather));

        when(openWeatherMapClient.getWeather(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        WeatherResponse result = weatherService.getWeather("London", "UK", "API_KEY_1");
        assertEquals("clear sky", result.getWeather().get(0).getDescription());
    }

    @Test
    public void testInvalidApiKey(){
        Exception exception = assertThrows(InvalidApiKeyException.class, () -> 
                weatherService.getWeather("London", "UK", "INVALID_API_KEY"));
        assertEquals("Invalid API Key.", exception.getMessage());
    }

    @Test
    public void testApiKeyRateLimitException(){
        for (int i = 0; i < 5; i++){
            weatherService.getWeather("London", "UK", "API_KEY_1");
        }
        Exception exception = assertThrows(ApiKeyLimitExceededException.class, () ->
                weatherService.getWeather("London", "UK", "API_KEY_1"));
        assertEquals("Hourly limit exceeded.", exception.getMessage());
    }

    @Test
    public void testNoWeatherDataReturned() {
        when(openWeatherMapClient.getWeather(Mockito.anyString(), Mockito.anyString())).thenReturn(new WeatherResponse());

        WeatherResponse result = weatherService.getWeather("London", "UK", "API_KEY_1");
        assertNull(result.getWeather());
    }

    @Test
    public void testWeatherDataStoredInDatabase() {
        Weather weather = new Weather();
        weather.setDescription("clear sky");

        WeatherResponse response = new WeatherResponse();
        response.setWeather(List.of(weather));

        when(openWeatherMapClient.getWeather(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

        weatherService.getWeather("London", "UK", "API_KEY_1");

        Mockito.verify(weatherRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testRateLimitResetsAfterOneHour() {
        for (int i = 0; i < 5; i++) {
            weatherService.getWeather("London", "UK", "API_KEY_1");
        }

        // Advance time by one hour
        Instant newInstant = Instant.parse("2024-07-24T12:00:00Z");
        when(clock.instant()).thenReturn(newInstant);

        assertDoesNotThrow(() -> weatherService.getWeather("London", "UK", "API_KEY_1"));
    }

    @Test
    public void testMissingCityField() {
        Exception exception = assertThrows(MissingFieldException.class, () ->
                weatherService.getWeather("", "UK", "API_KEY_1"));
        assertEquals("City field is missing.", exception.getMessage());
    }

    @Test
    public void testMissingCountryField() {
        Exception exception = assertThrows(MissingFieldException.class, () ->
                weatherService.getWeather("London", "", "API_KEY_1"));
        assertEquals("Country field is missing.", exception.getMessage());
    }

}
