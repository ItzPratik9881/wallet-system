package com.pratik.walletsystem.service.impl;

import com.pratik.walletsystem.dto.LoginRequest;
import com.pratik.walletsystem.dto.LoginResponse;
import com.pratik.walletsystem.entity.User;
import com.pratik.walletsystem.repository.UserRepository;
import com.pratik.walletsystem.security.JwtService;
import com.pratik.walletsystem.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(

                new UsernamePasswordAuthenticationToken(

                        request.getEmail(),

                        request.getPassword()

                )

        );

        User user = userRepository.findByEmail(request.getEmail())

                .orElseThrow(() -> new RuntimeException("User Not Found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()

                .token(token)

                .tokenType("Bearer")

                .email(user.getEmail())

                .fullName(user.getFullName())

                .role(user.getRole().name())

                .build();

    }

}