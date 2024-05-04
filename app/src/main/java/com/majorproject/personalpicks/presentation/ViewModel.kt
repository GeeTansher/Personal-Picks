package com.majorproject.personalpicks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majorproject.personalpicks.data.model.ProductsList
import com.majorproject.personalpicks.domain.repository.ProductsRepository
import com.majorproject.personalpicks.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {
    private val _getProductsResponseStateFlow: MutableStateFlow<NetworkResult<ProductsList>> =
        MutableStateFlow(NetworkResult.Loading())
    val getProductsResponseStateFlow: StateFlow<NetworkResult<ProductsList>> = _getProductsResponseStateFlow


    fun getProductsByCategory(category: String) = viewModelScope.launch {
        getProductsSafeCall(category)
    }

    private suspend fun getProductsSafeCall(category: String) {
        _getProductsResponseStateFlow.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = productsRepository.getProductsByCategory(category)
                _getProductsResponseStateFlow.value = handleBookingsResponse(response)
            } catch (e: Exception) {
                _getProductsResponseStateFlow.value = NetworkResult.Error(e.message)
            }
        } else {
            _getProductsResponseStateFlow.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun handleBookingsResponse(response: Response<ProductsList>): NetworkResult<ProductsList> {
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

    private fun hasInternetConnection(): Boolean {
        return true
    }



}