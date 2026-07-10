package com.pratik.walletsystem.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pratik.walletsystem.dto.UserRegistrationRequest;
import com.pratik.walletsystem.entity.User;
import com.pratik.walletsystem.entity.Wallet;
import com.pratik.walletsystem.enums.Role;
import com.pratik.walletsystem.enums.WalletStatus;
import com.pratik.walletsystem.exception.DuplicateEmailException;
import com.pratik.walletsystem.repository.UserRepository;
import com.pratik.walletsystem.repository.WalletRepository;
import com.pratik.walletsystem.service.UserService;
import com.pratik.walletsystem.exception.DuplicateEmailException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public String registerUser(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
             throw new DuplicateEmailException(
            "Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
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