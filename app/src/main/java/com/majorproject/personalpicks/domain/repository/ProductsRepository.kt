package com.majorproject.personalpicks.domain.repository

import com.majorproject.personalpicks.data.api.ProductsApi
import com.majorproject.personalpicks.data.model.ProductsList
import retrofit2.Response
import javax.inject.Inject


class ProductsRepository @Inject constructor(private val productsApi: ProductsApi) {
    suspend fun getProductsByCategory(category: String): Response<ProductsList> {
        return productsApi.getProductsByCategory(category)
    }
}