package com.example.registration_app.di

import com.example.registration_app.domain.repository.AuthRepository
import com.example.registration_app.domain.repository.StudentRegistrationRepository
import com.example.registration_app.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideSignInUseCase(authRepository: AuthRepository): SignInUseCase {
        return SignInUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSignUpUseCase(authRepository: AuthRepository): SignUpUseCase {
        return SignUpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSignOutUseCase(authRepository: AuthRepository): SignOutUseCase {
        return SignOutUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSendPasswordResetEmailUseCase(authRepository: AuthRepository): SendPasswordResetEmailUseCase {
        return SendPasswordResetEmailUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideVerifyPasswordResetCodeUseCase(authRepository: AuthRepository): VerifyPasswordResetCodeUseCase {
        return VerifyPasswordResetCodeUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideConfirmPasswordResetUseCase(authRepository: AuthRepository): ConfirmPasswordResetUseCase {
        return ConfirmPasswordResetUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGenerateOtpUseCase(authRepository: AuthRepository): GenerateOtpUseCase {
        return GenerateOtpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(authRepository: AuthRepository): VerifyOtpUseCase {
        return VerifyOtpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideChangePasswordUseCase(authRepository: AuthRepository): ChangePasswordUseCase {
        return ChangePasswordUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterStudentUseCase(
        studentRegistrationRepository: StudentRegistrationRepository
    ): RegisterStudentUseCase {
        return RegisterStudentUseCase(studentRegistrationRepository)
    }
}
