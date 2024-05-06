package com.majorproject.personalpicks.data.api

import com.majorproject.personalpicks.data.model.CustomerIdsResponse
import com.majorproject.personalpicks.data.model.ProductsList
import com.majorproject.personalpicks.data.model.SelectedProductsResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ProductsApi {

    @GET("/sendList")
    suspend fun getProductsByCategory(@Query("category") category: String): Response<ProductsList>

    @GET("/sendListCustomerId")
    suspend fun getCustomerIds(): Response<CustomerIdsResponse>

    @POST("/recommend")
    @Multipart
    suspend fun recommendProducts(
        @Part("interactionCategory") interactionPartCategory: RequestBody,
        @Part("interactionGlobal") interactionPartGlobal: RequestBody,
        @Part("category") category: RequestBody,
        @Part("userId") userId: RequestBody
    ): Response<SelectedProductsResponse>

}