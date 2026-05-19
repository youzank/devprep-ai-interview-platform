package com.devprep.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmitAnswerResponse {

    private Long questionId;
    private String userAnswer;
    private Boolean correct;
}