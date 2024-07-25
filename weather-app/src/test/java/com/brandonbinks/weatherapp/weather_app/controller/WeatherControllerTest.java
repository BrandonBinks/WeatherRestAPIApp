package com.brandonbinks.weatherapp.weather_app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.brandonbinks.weatherapp.weather_app.exception.ApiKeyLimitExceededException;
import com.brandonbinks.weatherapp.weather_app.exception.InvalidApiKeyException;
import com.brandonbinks.weatherapp.weather_app.model.WeatherResponse;
import com.brandonbinks.weatherapp.weather_app.service.WeatherService;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    public void testGetWeather() throws Exception {
        WeatherResponse.Weather weather = new WeatherResponse.Weather();
        weather.setDescription("broken clouds");

        WeatherResponse response = new WeatherResponse();
        response.setWeather(List.of(weather));

        when(weatherService.getWeather("Melbourne", "AUS", "API_KEY_1")).thenReturn(response);

        mockMvc.perform(get("/weather?city=Melbourne&country=AUS&apiKey=API_KEY_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weather[0].description").value("broken clouds"));

    }

    @Test
    public void testInvalidApiKey() throws Exception {
        when(weatherService.getWeather("Melbourne", "AUS", "INVALID_API_KEY")).thenThrow(new InvalidApiKeyException("Invalid API key."));

        mockMvc.perform(get("/weather?city=Melbourne&country=AUS&apiKey=INVALID_API_KEY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid API key."));
    }

    @Test
    public void testApiKeyRateLimitExceeded() throws Exception {
        when(weatherService.getWeather("Melbourne", "AUS", "API_KEY_1")).thenThrow(new ApiKeyLimitExceededException("Hourly limit exceeded."));

        mockMvc.perform(get("/weather?city=Melbourne&country=AUS&apiKey=API_KEY_1"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Hourly limit exceeded."));
    }

    @Test
    public void testMissingApiKey() throws Exception {
        mockMvc.perform(get("/weather?city=Melbourne&country=AUS"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testInvalidCityOrCountry() throws Exception {
        mockMvc.perform(get("/weather?city=&country=AUS&apiKey=API_KEY_1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("City field is missing."));;

        mockMvc.perform(get("/weather?city=Melbourne&country=&apiKey=API_KEY_1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Country field is missing."));
    }

    @Test
    public void testNoWeatherData() throws Exception {
        WeatherResponse response = new WeatherResponse();
        when(weatherService.getWeather("Melbourne", "AUS", "API_KEY_1")).thenReturn(response);

        mockMvc.perform(get("/weather?city=Melbourne&country=AUS&apiKey=API_KEY_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weather").isEmpty());
    }
    
}
