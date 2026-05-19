package com.devprep.backend.service;

import com.devprep.backend.dto.LoginRequest;
import com.devprep.backend.dto.RegisterRequest;
import com.devprep.backend.entity.User;
import com.devprep.backend.exception.EmailAlreadyExistsException;
import com.devprep.backend.exception.InvalidCredentialsException;
import com.devprep.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
           throw new EmailAlreadyExistsException("Email already exists!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return "User registered successfully!";
    }
    public String login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException("Invalid email or password!"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );
        if (!passwordMatches){
            throw new InvalidCredentialsException("Invalid email or password!");
        }

        return jwtService.generateToken(user.getEmail());
    }
}