package com.majorproject.personalpicks.data.model

data class SelectedProductsResponse(
    val categoryProductIds: List<Product>,
    val globalProductIds: List<Product>
)