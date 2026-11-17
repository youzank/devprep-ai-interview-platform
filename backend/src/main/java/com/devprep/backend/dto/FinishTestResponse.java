
package com.devprep.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinishTestResponse {

    private Long testSessionId;
    private long totalQuestions;
    private long correctAnswers;
    private long wrongAnswers;
    private long unansweredQuestions;
    private double score;
}
