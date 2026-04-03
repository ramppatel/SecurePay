package com.securepay.userservice.dto;

public record JwtResponse(
        String token,
        UserResponse user
) {
}
