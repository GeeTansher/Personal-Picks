package com.majorproject.personalpicks.data.api

import com.majorproject.personalpicks.data.model.ProductsList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApi {

    @GET("/productslist")
    suspend fun getProductsByCategory(@Query("category") category: String): Response<ProductsList>

    @POST("/recommend")
    suspend fun recommendProducts(@Query("category") category: String): Response<ProductsList>

}