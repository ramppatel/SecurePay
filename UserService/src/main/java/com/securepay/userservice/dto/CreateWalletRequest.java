package com.securepay.userservice.dto;

public record CreateWalletRequest(
        Long userId,
        String currency
) {
}
