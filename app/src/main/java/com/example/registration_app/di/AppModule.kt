package com.example.registration_app.di

import com.example.registration_app.data.datasource.FirebaseAuthDataSource
import com.example.registration_app.data.datasource.FirestoreDataSource
import com.example.registration_app.data.repository.AuthRepositoryImpl
import com.example.registration_app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthDataSource = FirebaseAuthDataSource(firebaseAuth)

    @Provides
    @Singleton
    fun provideFirestoreDataSource(
        firestore: FirebaseFirestore
    ): FirestoreDataSource = FirestoreDataSource(firestore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository = authRepositoryImpl
}
