package com.securepay.userservice.dto;

public record WalletResponse(
        Long id,
        Long userId,
        String currency,
        Long balance,
        Long availableBalance
) {
}
