package com.brandonbinks.weatherapp.weather_app.service;

import com.brandonbinks.weatherapp.weather_app.config.ApiKeyAuthentication;
import com.brandonbinks.weatherapp.weather_app.util.AppConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService {

    public static Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AppConstants.AUTH_TOKEN_HEADER_NAME);

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}