package com.brandonbinks.weatherapp.weather_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brandonbinks.weatherapp.weather_app.model.WeatherEntity;

import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Long>{
    Optional<WeatherEntity> findByCityAndCountry(String city, String country);
}
