package com.pratik.walletsystem.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pratik.walletsystem.dto.UserRegistrationRequest;
import com.pratik.walletsystem.entity.User;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.Role;
import com.pratik.walletsystem.enums.WalletStatus;
import com.pratik.walletsystem.exception.DuplicateEmailException;
import com.pratik.walletsystem.exception.DuplicatePhoneNumberException;
import com.pratik.walletsystem.repository.UserRepository;
import com.pratik.walletsystem.repository.WalletRepository;
import com.pratik.walletsystem.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String registerUser(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateEmailException(
                    "Email already registered: " + request.getEmail());
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber().trim())) {
            throw new DuplicatePhoneNumberException(
                    "Phone number already registered: " + request.getPhoneNumber());
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber().trim())
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .status(WalletStatus.ACTIVE)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        walletRepository.save(wallet);

        return "User Registered Successfully";
    }
}
