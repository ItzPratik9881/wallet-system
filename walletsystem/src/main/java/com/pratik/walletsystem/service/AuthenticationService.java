package com.pratik.walletsystem.service;

import com.pratik.walletsystem.dto.LoginRequest;
import com.pratik.walletsystem.dto.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

}