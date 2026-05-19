package com.devprep.backend.controller;

import com.devprep.backend.dto.*;
import com.devprep.backend.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/start")
    public StartTestResponse startTest(
            @RequestBody StartTestRequest request,
            Authentication authentication
    ) {
        return testService.startTest(request, authentication.getName());
    }

    @PostMapping("/{testSessionId}/answer")
    public SubmitAnswerResponse submitAnswer(
            @PathVariable Long testSessionId,
            @RequestBody SubmitAnswerRequest request,
            Authentication authentication
    ) {
        return testService.submitAnswer(
                testSessionId,
                request,
                authentication.getName()
        );
    }
}