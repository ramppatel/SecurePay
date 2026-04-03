package com.securepay.userservice.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String role,
        Instant createdAt
) {
}
