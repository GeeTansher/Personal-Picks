package com.majorproject.personalpicks.di

import android.app.Application
import android.content.Context
import com.majorproject.personalpicks.data.api.ProductsApi
import com.majorproject.personalpicks.domain.repository.ProductsRepository
import com.majorproject.personalpicks.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {


    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
    }

    @Provides
    @Singleton
    fun provideProductsRepository(apiService: ProductsApi): ProductsRepository {
        return ProductsRepository(apiService)
    }

    @Singleton
    @Provides
    fun providesProductsApi(retrofitBuilder: Retrofit.Builder): ProductsApi {
        return retrofitBuilder
            .build()
            .create(ProductsApi::class.java)
    }
}