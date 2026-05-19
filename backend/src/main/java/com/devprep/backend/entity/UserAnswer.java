package com.devprep.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long testSessionId;

    private Long questionId;

    private Long userId;

    private String userAnswer;

    private Boolean correct;

    private LocalDateTime answeredAt;
}