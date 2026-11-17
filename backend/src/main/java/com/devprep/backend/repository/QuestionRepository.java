package com.devprep.backend.repository;

import com.devprep.backend.entity.Question;
import com.devprep.backend.entity.QuestionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategory(QuestionCategory category);
    long countByTestSession_Id(Long testSessionId);
    Optional<Question> findByIdAndTestSession_Id(
            Long questionId,
            Long testSessionId
    );
}