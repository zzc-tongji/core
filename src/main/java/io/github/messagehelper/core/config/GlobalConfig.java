package io.github.messagehelper.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalConfig {
  @Bean
  public CorsFilter corsFilter(@Value("${setting.global-cors}") boolean cors) {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    if (cors) {
      corsConfiguration.addAllowedOrigin("*");
      corsConfiguration.addAllowedMethod("*");
      corsConfiguration.addAllowedHeader("*");
    }
    UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
    configurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsFilter(configurationSource);
  }
}