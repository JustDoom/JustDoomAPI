package com.imjustdoom.justdoomapi.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConfigurationProperties(prefix = "app")
@ConstructorBinding
@AllArgsConstructor
public class APIConfig {

    private final String storageUrl;
    private final String storageSecret;

    private final String frontendUrl;
    private final String backendUrl;

    private final boolean prodMode;
}
