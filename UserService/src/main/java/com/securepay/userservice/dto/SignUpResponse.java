package com.securepay.userservice.dto;

public record SignUpResponse(
        Long id,
        String fullName,
        String email,
        String role,
        boolean walletProvisioned
) {
}
