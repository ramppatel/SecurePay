package com.securepay.userservice.exception;

public class WalletProvisioningException extends RuntimeException {

    public WalletProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }
}
