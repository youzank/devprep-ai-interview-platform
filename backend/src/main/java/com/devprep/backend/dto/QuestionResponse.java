package com.devprep.backend.dto;

import com.devprep.backend.entity.Difficulty;
import com.devprep.backend.entity.QuestionCategory;
import com.devprep.backend.entity.QuestionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {

    private Long id;
    private String questionText;
    private QuestionCategory category;
    private QuestionType type;
    private Difficulty difficulty;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
}