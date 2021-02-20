package com.example.fotofinder.di

import com.example.fotofinder.api.UnsplashService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttp() = OkHttpClient.Builder().apply {
        addNetworkInterceptor(HttpLoggingInterceptor { message ->
            println("LOG-NET-INTERCEPTOR: $message")
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }.build()


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(MoshiConverterFactory.create().asLenient())
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideUnsplashService(retrofit: Retrofit): UnsplashService = retrofit.create(UnsplashService::class.java)

}