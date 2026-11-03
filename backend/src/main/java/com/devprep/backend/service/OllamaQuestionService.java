package com.devprep.backend.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OllamaQuestionService {

    private final RestClient restClient;

    public OllamaQuestionService() {
        this.restClient = RestClient.create("http://localhost:11434");
    }

    public String generate(String prompt) {
        var request = java.util.Map.of(
                "model", "llama3.2",
                "prompt", prompt,
                "stream", false
        );

        var response=  restClient.post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(java.util.Map.class);

        if(response == null || response.get("response")==null){
            throw new IllegalStateException("Ollama sıçtı");
         }
        return response.get("response").toString();

    }

}
