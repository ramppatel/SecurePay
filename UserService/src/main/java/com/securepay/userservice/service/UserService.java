package com.securepay.userservice.service;

import com.securepay.userservice.dto.JwtResponse;
import com.securepay.userservice.dto.LoginRequest;
import com.securepay.userservice.dto.SignUpRequest;
import com.securepay.userservice.dto.SignUpResponse;
import com.securepay.userservice.dto.UserResponse;
import java.util.List;

public interface UserService {

    SignUpResponse registerUser(SignUpRequest request);

    JwtResponse authenticate(LoginRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();
}
