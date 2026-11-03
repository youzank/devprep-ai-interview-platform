
package com.devprep.backend.controller;

import com.devprep.backend.service.OllamaQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ollama")
@RequiredArgsConstructor
public class OllamaTestController {

    private final OllamaQuestionService ollamaService;

    @GetMapping("/test")
    public String test() {
        return ollamaService.generate(
                "What is polymorphism in Java? Answer briefly."
        );
    }
}
