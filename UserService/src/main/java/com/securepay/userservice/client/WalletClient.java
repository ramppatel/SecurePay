package com.securepay.userservice.client;

import com.securepay.userservice.dto.CreateWalletRequest;
import com.securepay.userservice.dto.WalletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "walletServiceClient", url = "${services.wallet.base-url}")
@RequestMapping("/api/v1/wallets")
public interface WalletClient {

    @PostMapping
    WalletResponse createWallet(@RequestBody CreateWalletRequest request);
}
