package com.devprep.backend.service;

import com.devprep.backend.dto.CreateQuestionRequest;
import com.devprep.backend.entity.Question;
import com.devprep.backend.entity.QuestionCategory;
import com.devprep.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question createQuestion(CreateQuestionRequest request) {
        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .category(request.getCategory())
                .type(request.getType())
                .difficulty(request.getDifficulty())
                .optionA(request.getOptionA())
                .optionB(request.getOptionB())
                .optionC(request.getOptionC())
                .optionD(request.getOptionD())
                .correctAnswer(request.getCorrectAnswer())
                .build();

        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByCategory(QuestionCategory category) {
        return questionRepository.findByCategory(category);
    }
}