package com.devprep.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StartTestResponse {

    private Long testSessionId;
    private List<QuestionResponse> questions;
}