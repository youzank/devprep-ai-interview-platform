package com.devprep.backend.service;

import com.devprep.backend.dto.*;
import com.devprep.backend.entity.*;
import com.devprep.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final TestSessionRepository testSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final FakeAiQuestionService fakeAiQuestionService;

    public StartTestResponse startTest(StartTestRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TestSession testSession = TestSession.builder()
                .userId(user.getId())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .startedAt(LocalDateTime.now())
                .build();

        TestSession savedSession = testSessionRepository.save(testSession);

        List<Question> generatedQuestions = fakeAiQuestionService.generateQuestions(
                request.getCategory(),
                request.getDifficulty(),
                request.getQuestionCount()
        );

        List<Question> savedQuestions = questionRepository.saveAll(generatedQuestions);

        List<QuestionResponse> questionResponses = savedQuestions.stream()
                .map(this::mapToQuestionResponse)
                .toList();

        return StartTestResponse.builder()
                .testSessionId(savedSession.getId())
                .questions(questionResponses)
                .build();
    }

    public SubmitAnswerResponse submitAnswer(
            Long testSessionId,
            SubmitAnswerRequest request,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        boolean correct = question.getCorrectAnswer()
                .equalsIgnoreCase(request.getUserAnswer());

        UserAnswer userAnswer = UserAnswer.builder()
                .testSessionId(testSessionId)
                .questionId(question.getId())
                .userId(user.getId())
                .userAnswer(request.getUserAnswer())
                .correct(correct)
                .answeredAt(LocalDateTime.now())
                .build();

        userAnswerRepository.save(userAnswer);

        return SubmitAnswerResponse.builder()
                .questionId(question.getId())
                .userAnswer(request.getUserAnswer())
                .correct(correct)
                .build();
    }

    private QuestionResponse mapToQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .category(question.getCategory())
                .type(question.getType())
                .difficulty(question.getDifficulty())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .build();
    }
}