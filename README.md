# Registration App with Firebase Authentication

This is an Android application implementing Login and Sign Up with Firebase Authentication using Clean Architecture, MVI (Model-View-Intent) pattern, and Dagger Hilt for dependency injection.

## Architecture

The app follows **Clean Architecture** principles with three main layers:

### 1. **Domain Layer** (`domain/`)
- **Entities**: `User.kt`, `AuthResult.kt`
- **Repository Interfaces**: `AuthRepository.kt`
- **Use Cases**: 
  - `SignInUseCase.kt`
  - `SignUpUseCase.kt`
  - `GetCurrentUserUseCase.kt`
  - `SignOutUseCase.kt`

### 2. **Data Layer** (`data/`)
- **Data Sources**: `FirebaseAuthDataSource.kt`
- **Repository Implementations**: `AuthRepositoryImpl.kt`

### 3. **Presentation Layer** (`presentation/`)
- **MVI Pattern**:
  - **State**: `LoginState.kt`, `SignUpState.kt`, `HomeState.kt`
  - **Intent**: `LoginIntent.kt`, `SignUpIntent.kt`
  - **ViewModels**: `LoginViewModel.kt`, `SignUpViewModel.kt`, `HomeViewModel.kt`
- **UI Screens**: `LoginScreen.kt`, `SignUpScreen.kt`, `HomeScreen.kt`
- **Navigation**: `NavGraph.kt`

## Tech Stack

- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVI Pattern
- **Dependency Injection**: Dagger Hilt
- **Authentication**: Firebase Authentication
- **Navigation**: Navigation Compose
- **Async**: Kotlin Coroutines & Flow
- **State Management**: StateFlow

## Setup Instructions

### 1. Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Add an Android app to your project:
   - Package name: `com.example.registration_app`
   - App nickname: Registration App (optional)
   - Debug signing certificate SHA-1 (optional for development)
4. Download the `google-services.json` file
5. Replace the placeholder `app/google-services.json` file with your downloaded file

### 2. Enable Firebase Authentication

1. In Firebase Console, go to **Authentication**
2. Click **Get Started**
3. Enable **Email/Password** sign-in method
4. Save the changes

### 3. Build the Project

1. Sync the project with Gradle files
2. Build and run the app

## Project Structure

```
app/src/main/java/com/example/registration_app/
├── data/
│   ├── datasource/
│   │   └── FirebaseAuthDataSource.kt
│   └── repository/
│       └── AuthRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   └── AuthResult.kt
│   ├── repository/
│   │   └── AuthRepository.kt
│   └── usecase/
│       ├── SignInUseCase.kt
│       ├── SignUpUseCase.kt
│       ├── GetCurrentUserUseCase.kt
│       └── SignOutUseCase.kt
├── presentation/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeState.kt
│   │   └── HomeViewModel.kt
│   ├── login/
│   │   ├── LoginScreen.kt
│   │   ├── LoginState.kt
│   │   ├── LoginIntent.kt
│   │   ├── LoginViewModel.kt
│   │   └── LoginRoute.kt
│   ├── signup/
│   │   ├── SignUpScreen.kt
│   │   ├── SignUpState.kt
│   │   ├── SignUpIntent.kt
│   │   ├── SignUpViewModel.kt
│   │   └── SignUpRoute.kt
│   └── navigation/
│       └── NavGraph.kt
├── di/
│   ├── AppModule.kt
│   └── UseCaseModule.kt
├── MainActivity.kt
└── RegistrationApp.kt
```

## MVI Pattern Implementation

The app uses the **MVI (Model-View-Intent)** pattern:

### Intent (User Actions)
- User interactions are represented as `Intent` sealed classes
- Examples: `UpdateEmail`, `UpdatePassword`, `SignIn`, `SignUp`

### State (UI State)
- All UI state is stored in immutable `State` data classes
- State changes are reactive through `StateFlow`

### ViewModel
- Processes `Intent`s and updates `State`
- Contains business logic and calls use cases
- Uses `handleIntent()` method to process user actions

### View (Composable Screens)
- Observes `State` using `collectAsStateWithLifecycle()`
- Sends `Intent`s to ViewModel through `handleIntent()`
- Updates UI based on current state

## Features

- ✅ Email/Password Authentication
- ✅ User Registration
- ✅ User Login
- ✅ User Profile Display
- ✅ Sign Out Functionality
- ✅ Error Handling
- ✅ Loading States
- ✅ Form Validation
- ✅ Navigation between screens

## Dependencies

Key dependencies used:
- Firebase Authentication
- Dagger Hilt for DI
- Jetpack Compose
- Navigation Compose
- Coroutines & Flow
- Material 3

## Notes

- The `google-services.json` file is currently a placeholder. You **must** replace it with your actual Firebase configuration file.
- Minimum SDK: 28 (Android 9.0)
- Target SDK: 36

## License

This project is for educational purposes.
