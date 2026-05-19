package com.devprep.backend.repository;

import com.devprep.backend.entity.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
}