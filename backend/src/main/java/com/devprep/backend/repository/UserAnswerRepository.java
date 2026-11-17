package com.devprep.backend.repository;

import com.devprep.backend.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    boolean existsByTestSessionIdAndQuestionId(
            Long testSessionId,
            Long questionId
    );
    long countByTestSessionId(Long testSessionId);

    long countByTestSessionIdAndCorrectTrue(Long testSessionId);
}