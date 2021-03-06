package dev.rexijie.ukgovapi.config;

import dev.rexijie.ukgovapi.errors.CustomErrorAttributes;
import dev.rexijie.ukgovapi.errors.CustomErrorWebExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFlux
public class WebConfig {

    public ErrorAttributes customErrorAttributes() {
        return new CustomErrorAttributes(
                new DefaultErrorAttributes()
        );
    }

    /**
     * Include custom error handler
     */
    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ServerProperties serverProperties,
                                                             WebProperties webProperties,
                                                             ObjectProvider<ViewResolver> viewResolvers,
                                                             ServerCodecConfigurer serverCodecConfigurer,
                                                             ApplicationContext applicationContext) {
        DefaultErrorWebExceptionHandler exceptionHandler = new CustomErrorWebExceptionHandler(customErrorAttributes(),
                webProperties.getResources(),
                serverProperties.getError(),
                applicationContext);
        exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
        exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

    @Bean
    CorsWebFilter corsWebFilter(ApplicationProperties properties) {
        var allowedMethods = StringUtils.tokenizeToStringArray(
                properties.cors().allowedMethods(), ",", true, true);
        var allowedOrigins = StringUtils.tokenizeToStringArray(
                properties.cors().allowedOrigins(), ",", true, true);
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigins));
        corsConfig.setMaxAge(properties.cors().maxAge());
        corsConfig.setAllowedMethods(Arrays.asList(allowedMethods));
        corsConfig.addAllowedHeader("XX-API-ALLOWED");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
