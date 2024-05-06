package com.majorproject.personalpicks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majorproject.personalpicks.data.model.CustomerIdsResponse
import com.majorproject.personalpicks.data.model.Product
import com.majorproject.personalpicks.data.model.ProductsList
import com.majorproject.personalpicks.data.model.SelectedProductsResponse
import com.majorproject.personalpicks.domain.repository.ProductsRepository
import com.majorproject.personalpicks.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {
    private val _getProductsResponseStateFlow: MutableStateFlow<NetworkResult<ProductsList>> =
        MutableStateFlow(NetworkResult.Loading())
    val getProductsResponseStateFlow: StateFlow<NetworkResult<ProductsList>> = _getProductsResponseStateFlow

    private val _getCustomerIdResponseStateFlow: MutableStateFlow<NetworkResult<CustomerIdsResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val getCustomerIdResponseStateFlow: MutableStateFlow<NetworkResult<CustomerIdsResponse>> = _getCustomerIdResponseStateFlow


    private val _getRecommendationResponseStateFlow: MutableStateFlow<NetworkResult<SelectedProductsResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val getRecommendationResponseStateFlow: StateFlow<NetworkResult<SelectedProductsResponse>> = _getRecommendationResponseStateFlow

    lateinit var globalList: List<Product>
    lateinit var categoryList: List<Product>


    fun getProductsByCategory(category: String) = viewModelScope.launch {
        getProductsSafeCall(category)
    }

    private suspend fun getProductsSafeCall(category: String) {
        _getProductsResponseStateFlow.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = productsRepository.getProductsByCategory(category)
                _getProductsResponseStateFlow.value = handleProductsResponse(response)
            } catch (e: Exception) {
                _getProductsResponseStateFlow.value = NetworkResult.Error(e.message)
            }
        } else {
            _getProductsResponseStateFlow.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleProductsResponse(response: Response<ProductsList>): NetworkResult<ProductsList> {
        return when {
            response.message().toString().contains("timeout") -> NetworkResult.Error("Timeout")
            response.code() == 402 -> NetworkResult.Error("API Key Limit Exceeded")
            response.isSuccessful -> {
                val res = response.body()
                NetworkResult.Success(res!!)
            }
            else -> NetworkResult.Error(response.message())
        }
    }

    fun getCustomerIds() = viewModelScope.launch {
        getCustomerIdsSafeCall()
    }

    private suspend fun getCustomerIdsSafeCall() {
        _getCustomerIdResponseStateFlow.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = productsRepository.getCustomerIds()
                Log.d("Response", response.toString())
                _getCustomerIdResponseStateFlow.value = handleCustomerIdResponse(response)
            } catch (e: Exception) {
                _getCustomerIdResponseStateFlow.value = NetworkResult.Error(e.message)
            }
        } else {
            _getCustomerIdResponseStateFlow.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleCustomerIdResponse(response: Response<CustomerIdsResponse>): NetworkResult<CustomerIdsResponse> {
        return when {
            response.message().toString().contains("timeout") -> NetworkResult.Error("Timeout")
            response.code() == 402 -> NetworkResult.Error("API Key Limit Exceeded")
            response.isSuccessful -> {
                val res = response.body()
                NetworkResult.Success(res!!)
            }
            else -> NetworkResult.Error(response.message())
        }
    }


    fun getRecommendations(interactionPartCategory: RequestBody, interactionPartGlobal: RequestBody, category: RequestBody, userId:RequestBody) = viewModelScope.launch {
        getRecommendationsSafeCall(interactionPartCategory, interactionPartGlobal, category, userId)
    }

    private suspend fun getRecommendationsSafeCall(interactionPartCategory: RequestBody, interactionPartGlobal: RequestBody, category: RequestBody, userId:RequestBody) {
        _getRecommendationResponseStateFlow.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = productsRepository.recommendProducts(interactionPartCategory, interactionPartGlobal, category, userId)
                _getRecommendationResponseStateFlow.value = handleRecommendationResponse(response)
            } catch (e: Exception) {
                _getRecommendationResponseStateFlow.value = NetworkResult.Error(e.message)
            }
        } else {
            _getRecommendationResponseStateFlow.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleRecommendationResponse(response: Response<SelectedProductsResponse>): NetworkResult<SelectedProductsResponse> {
        return when {
            response.message().toString().contains("timeout") -> NetworkResult.Error("Timeout")
            response.code() == 402 -> NetworkResult.Error("API Key Limit Exceeded")
            response.isSuccessful -> {
                val res = response.body()
                Log.d("Response", res.toString())
                NetworkResult.Success(res!!)
            }
            else -> NetworkResult.Error(response.message())
        }
    }

    private fun hasInternetConnection(): Boolean {
        return true
    }



}