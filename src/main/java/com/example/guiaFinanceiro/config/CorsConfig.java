package com.example.guiaFinanceiro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite credenciais (cookies/auth headers)
        config.setAllowCredentials(true);

        // IMPORTANTE: Se usar allowCredentials(true), não pode usar "*" no allowedOrigins.
        // O Spring precisa de padrões específicos ou listar as origens.
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // Permite GET, POST, PUT, DELETE, OPTIONS, etc.

        // Registra a configuração para todos os caminhos da API
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    /*
   @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("https://api-william.duckdns.org");
        devServer.setDescription("Servidor de Produção VPS");

        return new OpenAPI().servers(List.of(devServer));
    }
    */

}