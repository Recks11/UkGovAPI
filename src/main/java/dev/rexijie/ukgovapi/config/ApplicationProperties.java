package dev.rexijie.ukgovapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "app.properties")
public record ApplicationProperties(CorsConfiguration cors) {

    static record CorsConfiguration(long maxAge,
                                    String allowedMethods,
                                    String allowedOrigins) {
    }
}

