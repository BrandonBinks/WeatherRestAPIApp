package com.brandonbinks.weatherapp.weather_app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    private List<Weather> weather;

    public List<Weather> getWeather(){
        return weather;
    }

    public void setWeather(List<Weather> weather){
        this.weather = weather;
    }

    public static class Weather{
        private String description;

        public String getDescription(){
            return description;
        }

        public void setDescription(String description){
            this.description = description;
        }
    }
    
}
