package com.devprep.backend.service;

import com.devprep.backend.entity.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiQuestionService {

    public List<Question> generateQuestions(
            QuestionCategory category,
            Difficulty difficulty,
            Integer count
    ) {
        List<Question> questions = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            Question question = Question.builder()
                    .questionText("AI generated sample question " + i + " for " + category)
                    .category(category)
                    .difficulty(difficulty)
                    .type(QuestionType.MULTIPLE_CHOICE)
                    .optionA("Option A")
                    .optionB("Option B")
                    .optionC("Option C")
                    .optionD("Option D")
                    .correctAnswer("A")
                    .build();

            questions.add(question);
        }

        return questions;
    }
}