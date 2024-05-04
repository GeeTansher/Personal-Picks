package com.majorproject.personalpicks.di

import com.majorproject.personalpicks.data.api.ProductsApi
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

    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constants.BASE_URL)
    }

    @Singleton
    @Provides
    fun providesProductsApi(retrofitBuilder: Retrofit.Builder): ProductsApi {
        return retrofitBuilder
            .build()
            .create(ProductsApi::class.java)
    }
}