package com.brandonbinks.weatherapp.weather_app.service;

import com.brandonbinks.weatherapp.weather_app.exception.ApiKeyLimitExceededException;
import com.brandonbinks.weatherapp.weather_app.exception.InvalidApiKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ApiServiceTest {

    @Autowired
    private ApiService apiService;

    @MockBean
    private Clock clock;

    @BeforeEach
    public void setup() {
        Instant fixedInstant = Instant.parse("2024-07-24T10:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }

    @Test
    public void testValidateApiKey() {
        apiService.validateApiKey("API_KEY_1");
    }

    @Test
    public void testInvalidApiKey() {
        Exception exception = assertThrows(InvalidApiKeyException.class, () ->
                apiService.validateApiKey("INVALID_API_KEY"));
        assertEquals("Invalid API Key.", exception.getMessage());
    }

    @Test
    public void testEnforceRateLimit() {
        for (int i = 0; i < 5; i++) {
            apiService.enforceRateLimit("API_KEY_2");
        }

        Exception exception = assertThrows(ApiKeyLimitExceededException.class, () ->
                apiService.enforceRateLimit("API_KEY_2"));
        assertEquals("Hourly limit exceeded.", exception.getMessage());
    }

    @Test
    public void testRateLimitResetsAfterOneHour() {
        for (int i = 0; i < 5; i++) {
            apiService.enforceRateLimit("API_KEY_3");
        }

        // Advance time by one hour
        Instant newInstant = Instant.parse("2024-07-24T11:00:00Z");
        when(clock.instant()).thenReturn(newInstant);

        assertDoesNotThrow(() -> apiService.enforceRateLimit("API_KEY_3"));
    }
}
