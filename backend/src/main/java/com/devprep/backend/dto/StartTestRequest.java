package com.devprep.backend.dto;

import com.devprep.backend.entity.Difficulty;
import com.devprep.backend.entity.QuestionCategory;
import lombok.Data;

@Data
public class StartTestRequest {

    private QuestionCategory category;
    private Difficulty difficulty;
    private Integer questionCount;
}