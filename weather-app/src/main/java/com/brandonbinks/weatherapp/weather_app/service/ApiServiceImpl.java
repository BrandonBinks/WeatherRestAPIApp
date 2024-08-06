package com.brandonbinks.weatherapp.weather_app.service;

import com.brandonbinks.weatherapp.weather_app.exception.ApiKeyLimitExceededException;
import com.brandonbinks.weatherapp.weather_app.exception.InvalidApiKeyException;
import com.brandonbinks.weatherapp.weather_app.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ApiServiceImpl implements ApiService {

    private final Clock clock;

    private static final int RATE_LIMIT = 5;
    private static final ConcurrentHashMap<String, AtomicInteger> apiKeyUsage = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LocalDateTime> apiKeyTimestamp = new ConcurrentHashMap<>();


    @Autowired
    public ApiServiceImpl(Clock clock) {this.clock = clock;}

    @Override
    public void validateApiKey(String apiKey) {
        if (!AppConstants.VALID_API_KEYS.contains(apiKey)) {
            throw new InvalidApiKeyException("Invalid API Key.");
        }
    }

    @Override
    public void enforceRateLimit(String apiKey) {
        LocalDateTime now = LocalDateTime.now(clock);
        apiKeyUsage.putIfAbsent(apiKey, new AtomicInteger(0));
        apiKeyTimestamp.putIfAbsent(apiKey, now);

        if (apiKeyUsage.get(apiKey).get() >= RATE_LIMIT &&
                apiKeyTimestamp.get(apiKey).isAfter(now.minusHours(1))) {
            throw new ApiKeyLimitExceededException("Hourly limit exceeded.");
        }

        if (apiKeyTimestamp.get(apiKey).isBefore(now.minusHours(1))) {
            apiKeyUsage.get(apiKey).set(0);
            apiKeyTimestamp.put(apiKey, now);
        }

        apiKeyUsage.get(apiKey).incrementAndGet();
    }
}