package com.majorproject.personalpicks.domain.repository

import android.util.Log
import com.majorproject.personalpicks.data.api.ProductsApi
import com.majorproject.personalpicks.data.model.CustomerIdsResponse
import com.majorproject.personalpicks.data.model.ProductsList
import com.majorproject.personalpicks.data.model.SelectedProductsResponse
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject


class ProductsRepository @Inject constructor(private val productsApi: ProductsApi) {
    suspend fun getProductsByCategory(category: String): Response<ProductsList> {
        return productsApi.getProductsByCategory(category)
    }

    suspend fun getCustomerIds(): Response<CustomerIdsResponse> {
        return productsApi.getCustomerIds()
    }

    suspend fun recommendProducts(
        interactionPartCategory: RequestBody,
        interactionPartGlobal: RequestBody,
        category: RequestBody,
        userId: RequestBody
    ): Response<SelectedProductsResponse> {
        return  productsApi.recommendProducts(interactionPartCategory, interactionPartGlobal, category, userId)
    }
}