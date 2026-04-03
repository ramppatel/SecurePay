# SecurePay Architecture

SecurePay is being organized around a renamed version of the reference microservice layout.

Planned services:

- `ApiGateway`
- `NotificationService`
- `RewardService`
- `TransactionService`
- `UserService`
- `WalletService`

Implemented in this pass:

- `UserService` with signup, login, JWT security, H2 persistence, and wallet provisioning hooks.

Scaffolded only:

- `ApiGateway`
- `NotificationService`
- `RewardService`
- `TransactionService`
- `WalletService`
