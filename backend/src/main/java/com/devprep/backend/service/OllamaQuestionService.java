
package com.devprep.backend.service;

import com.devprep.backend.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OllamaQuestionService {


    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;


    public OllamaQuestionService(
            ObjectMapper objectMapper,
            @Value("${ollama.url}") String ollamaUrl,
            @Value("${ollama.model}") String model,
            @Value("${ollama.connect-timeout}") int connectTimeout,
            @Value("${ollama.read-timeout}") int readTimeout
    ) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(ollamaUrl)
                .requestFactory(factory)
                .build();

        this.objectMapper = objectMapper;
        this.model = model;
    }

    public List<Question> generateQuestions(
            QuestionCategory category,
            Difficulty difficulty,
            Integer count
    ) {
        if (category == null || difficulty == null ||
                count == null || count < 1 || count > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid test parameters"
            );
        }

        String prompt = """
                Generate exactly %d technical interview
                multiple-choice questions.

                Topic: %s
                Difficulty: %s

                Return ONLY valid JSON in this structure:
                {
                  "questions": [
                    {
                      "questionText": "Question text",
                      "optionA": "First option",
                      "optionB": "Second option",
                      "optionC": "Third option",
                      "optionD": "Fourth option",
                      "correctAnswer": "A"
                    }
                  ]
                }

                Rules:
                - Exactly four options per question.
                - correctAnswer must be A, B, C or D.
                - Only one option must be correct.
                - Questions must be technically accurate.
                - Do not repeat questions.
                - No markdown or explanations outside JSON.
                """.formatted(count, category, difficulty);


        Map<String, Object> questionSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "questionText", Map.of("type", "string"),
                        "optionA", Map.of("type", "string"),
                        "optionB", Map.of("type", "string"),
                        "optionC", Map.of("type", "string"),
                        "optionD", Map.of("type", "string"),
                        "correctAnswer", Map.of(
                                "type", "string",
                                "enum", List.of("A", "B", "C", "D")
                        )
                ),
                "required", List.of(
                        "questionText",
                        "optionA",
                        "optionB",
                        "optionC",
                        "optionD",
                        "correctAnswer"
                ),
                "additionalProperties", false
        );

        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "questions", Map.of(
                                "type", "array",
                                "minItems", count,
                                "maxItems", count,
                                "items", questionSchema
                        )
                ),
                "required", List.of("questions"),
                "additionalProperties", false
        );

        Map<String, Object> request = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false,
                "format", schema,
                "options", Map.of("temperature", 0.2)
        );


        try {
            String rawResponse = restClient.post()
                    .uri("/api/generate")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            JsonNode ollamaResponse =
                    objectMapper.readTree(rawResponse);

            String generatedText =
                    ollamaResponse.path("response").asText();

            JsonNode questionsNode = objectMapper
                    .readTree(generatedText)
                    .path("questions");

            if (!questionsNode.isArray() ||
                    questionsNode.size() != count) {
                throw new IllegalStateException(
                        "Invalid number of generated questions"
                );
            }

            List<Question> questions = new ArrayList<>();

            for (JsonNode node : questionsNode) {
                String text = required(node, "questionText");
                String a = required(node, "optionA");
                String b = required(node, "optionB");
                String c = required(node, "optionC");
                String d = required(node, "optionD");
                String correct = required(node, "correctAnswer")
                        .toUpperCase();

                if (!List.of("A", "B", "C", "D").contains(correct)) {
                    throw new IllegalStateException(
                            "Invalid correct answer: " + correct
                    );
                }

                Question question = Question.builder()
                        .questionText(text)
                        .category(category)
                        .difficulty(difficulty)
                        .type(QuestionType.MULTIPLE_CHOICE)
                        .optionA(a)
                        .optionB(b)
                        .optionC(c)
                        .optionD(d)
                        .correctAnswer(correct)
                        .build();

                questions.add(question);
            }

            return questions;

        }
        catch (ResourceAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Ollama is unavailable or timed out",
                    e
            );

        } catch (Exception e) {
            e.printStackTrace();

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Ollama question generation failed",
                    e
            );
        }

    }

    private String required(JsonNode node, String field) {
        JsonNode value = node.path(field);

        if (!value.isTextual() || value.asText().isBlank()) {
            throw new IllegalStateException(
                    "Missing or invalid field: " + field
            );
        }

        return value.asText().trim();
    }
}
