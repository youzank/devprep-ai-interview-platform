package com.devprep.backend.controller;

import com.devprep.backend.dto.CreateQuestionRequest;
import com.devprep.backend.entity.Question;
import com.devprep.backend.entity.QuestionCategory;
import com.devprep.backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public Question createQuestion(@RequestBody CreateQuestionRequest request) {
        return questionService.createQuestion(request);
    }

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/category/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable QuestionCategory category) {
        return questionService.getQuestionsByCategory(category);
    }
}