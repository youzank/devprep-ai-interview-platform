package com.devprep.backend.controller;

import com.devprep.backend.dto.RegisterRequest;
import com.devprep.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
            String response = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}