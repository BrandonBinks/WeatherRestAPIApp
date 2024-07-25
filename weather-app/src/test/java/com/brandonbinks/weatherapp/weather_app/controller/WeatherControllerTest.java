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
    
}
