package com.devprep.backend.service;

import com.devprep.backend.dto.*;
import com.devprep.backend.entity.*;
import com.devprep.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final TestSessionRepository testSessionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final OllamaQuestionService ollamaQuestionService;


    @Transactional
    public StartTestResponse startTest(
            StartTestRequest request,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        List<Question> generatedQuestions =
                ollamaQuestionService.generateQuestions(
                        request.getCategory(),
                        request.getDifficulty(),
                        request.getQuestionCount()
                );

        TestSession testSession = TestSession.builder()
                .userId(user.getId())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .startedAt(LocalDateTime.now())
                .build();

        TestSession savedSession =
                testSessionRepository.save(testSession);

        generatedQuestions.forEach(
                question -> question.setTestSession(savedSession)
        );

        List<Question> savedQuestions =
                questionRepository.saveAll(generatedQuestions);

        List<QuestionResponse> questionResponses =
                savedQuestions.stream()
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        TestSession session = testSessionRepository
                .findById(testSessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Test session not found"
                ));

        // Check test ownership
        if (!session.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this test session"
            );
        }

        // Check test completion
        if (session.getCompletedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Test session is already completed"
            );
        }

        // Validate request
        if (request == null ||
                request.getQuestionId() == null ||
                request.getUserAnswer() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Question ID and answer are required"
            );
        }

        String answer = request.getUserAnswer()
                .trim()
                .toUpperCase(java.util.Locale.ROOT);

        if (!List.of("A", "B", "C", "D").contains(answer)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Answer must be A, B, C or D"
            );
        }

        // Check question ownership
        Question question = questionRepository
                .findByIdAndTestSession_Id(
                        request.getQuestionId(),
                        testSessionId
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Question does not belong to this test"
                ));

        // Prevent duplicate answers
        if (userAnswerRepository.existsByTestSessionIdAndQuestionId(
                testSessionId, question.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Question already answered"
            );
        }

        boolean correct = question.getCorrectAnswer()
                .equalsIgnoreCase(answer);

        UserAnswer userAnswer = UserAnswer.builder()
                .testSessionId(testSessionId)
                .questionId(question.getId())
                .userId(user.getId())
                .userAnswer(answer)
                .correct(correct)
                .answeredAt(LocalDateTime.now())
                .build();

        userAnswerRepository.save(userAnswer);

        return SubmitAnswerResponse.builder()
                .questionId(question.getId())
                .userAnswer(answer)
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

    @Transactional
    public FinishTestResponse finishTest(
            Long testSessionId,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        TestSession session = testSessionRepository
                .findById(testSessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Test session not found"
                ));

        if (!session.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not own this test session"
            );
        }

        if (session.getCompletedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Test already completed"
            );
        }

        long total = questionRepository
                .countByTestSession_Id(testSessionId);

        long answered = userAnswerRepository
                .countByTestSessionId(testSessionId);

        long correct = userAnswerRepository
                .countByTestSessionIdAndCorrectTrue(testSessionId);

        if (total == 0 || answered > total) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Invalid test data"
            );
        }

        long wrong = answered - correct;
        long unanswered = total - answered;

        double score = (correct * 100.0) / total;

        session.setCompletedAt(LocalDateTime.now());
        testSessionRepository.save(session);

        return FinishTestResponse.builder()
                .testSessionId(testSessionId)
                .totalQuestions(total)
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .unansweredQuestions(unanswered)
                .score(score)
                .build();
    }

}