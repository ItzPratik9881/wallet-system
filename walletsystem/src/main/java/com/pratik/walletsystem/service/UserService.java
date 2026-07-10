package com.pratik.walletsystem.service;

import com.pratik.walletsystem.dto.UserRegistrationRequest;

public interface UserService {

    String registerUser(UserRegistrationRequest request);

}