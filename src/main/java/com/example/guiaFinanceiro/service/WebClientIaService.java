package com.example.guiaFinanceiro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
@Service
public class WebClientIaService {

    private final WebClient webClient;

    @Value("${groq.api.key}")
    private String apiKey;

    public WebClientIaService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.groq.com/openai/v1")
                .build();
    }

    public String getFinancialAdvice(String context) {

        if (context == null || context.isBlank()) {
            context = "Me dê uma dica simples de economia financeira.";
        }

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "Você é um consultor financeiro."),
                        Map.of("role", "user",
                                "content", context)
                )
        );

        try {

            Map response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List choices = (List) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "Não consegui gerar uma dica no momento.";
            }

            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");

            return (String) message.get("content");

        } catch (Exception e) {
            return "Continue economizando e mantendo disciplina financeira.";
        }
    }
}