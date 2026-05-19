package com.devprep.backend.dto;

import lombok.Data;

@Data
public class SubmitAnswerRequest {

    private Long questionId;
    private String userAnswer;
}