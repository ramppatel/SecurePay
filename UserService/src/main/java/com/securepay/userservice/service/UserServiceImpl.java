package com.securepay.userservice.service;

import com.securepay.userservice.client.WalletClient;
import com.securepay.userservice.config.WalletServiceProperties;
import com.securepay.userservice.dto.CreateWalletRequest;
import com.securepay.userservice.dto.JwtResponse;
import com.securepay.userservice.dto.LoginRequest;
import com.securepay.userservice.dto.SignUpRequest;
import com.securepay.userservice.dto.SignUpResponse;
import com.securepay.userservice.dto.UserResponse;
import com.securepay.userservice.entity.UserAccount;
import com.securepay.userservice.entity.UserRole;
import com.securepay.userservice.exception.AuthenticationFailedException;
import com.securepay.userservice.exception.DuplicateUserException;
import com.securepay.userservice.exception.UserNotFoundException;
import com.securepay.userservice.exception.WalletProvisioningException;
import com.securepay.userservice.repository.UserAccountRepository;
import com.securepay.userservice.util.JwtService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletClient walletClient;
    private final WalletServiceProperties walletServiceProperties;

    public UserServiceImpl(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            WalletClient walletClient,
            WalletServiceProperties walletServiceProperties
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletClient = walletClient;
        this.walletServiceProperties = walletServiceProperties;
    }

    @Override
    @Transactional
    public SignUpResponse registerUser(SignUpRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userAccountRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateUserException("A user with that email already exists.");
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setFullName(request.fullName().trim());
        userAccount.setEmail(normalizedEmail);
        userAccount.setPasswordHash(passwordEncoder.encode(request.password()));
        userAccount.setRole(UserRole.ROLE_USER);

        UserAccount savedUser = userAccountRepository.save(userAccount);
        boolean walletProvisioned = false;

        if (walletServiceProperties.isEnabled()) {
            try {
                walletClient.createWallet(new CreateWalletRequest(
                        savedUser.getId(),
                        walletServiceProperties.getDefaultCurrency()
                ));
                walletProvisioned = true;
            } catch (Exception ex) {
                userAccountRepository.deleteById(savedUser.getId());
                throw new WalletProvisioningException(
                        "User creation was rolled back because wallet provisioning failed.",
                        ex
                );
            }
        }

        return new SignUpResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                walletProvisioned
        );
    }

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(request.email().trim())
                .orElseThrow(() -> new AuthenticationFailedException("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), userAccount.getPasswordHash())) {
            throw new AuthenticationFailedException("Invalid email or password.");
        }

        return new JwtResponse(jwtService.generateToken(userAccount), toResponse(userAccount));
    }

    @Override
    public UserResponse getUserById(Long id) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id " + id + "."));
        return toResponse(userAccount);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for email " + email + "."));
        return toResponse(userAccount);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userAccountRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(UserAccount userAccount) {
        return new UserResponse(
                userAccount.getId(),
                userAccount.getFullName(),
                userAccount.getEmail(),
                userAccount.getRole().name(),
                userAccount.getCreatedAt()
        );
    }
}
