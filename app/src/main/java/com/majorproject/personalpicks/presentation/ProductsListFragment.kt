package com.majorproject.personalpicks.presentation

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.majorproject.personalpicks.R
import com.majorproject.personalpicks.ViewModel
import com.majorproject.personalpicks.data.model.Product
import com.majorproject.personalpicks.databinding.FragmentProductsListBinding
import com.majorproject.personalpicks.domain.adapter.ProductItemAdapter
import com.majorproject.personalpicks.domain.adapter.SelectedProductItemAdapter
import com.majorproject.personalpicks.utils.NetworkListener
import com.majorproject.personalpicks.utils.NetworkResult
import com.majorproject.personalpicks.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

//    private val viewModel: ViewModel by viewModels()

    private lateinit var viewModel: ViewModel

    private var _binding: FragmentProductsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkListener: NetworkListener
    private val categories: Array<String> = arrayOf("Apparel",
        "Beauty",
        "GiftCard",
        "Jewelry",
        "Watches",
        "Shoes",
        "Luggage",
        "Global")

    @Inject
    lateinit var tokenManager: TokenManager

    private var productListCategory: MutableList<Product> = emptyList<Product>().toMutableList()
    private var productListGlobal: MutableList<Product> = emptyList<Product>().toMutableList()

    private val productAdapter by lazy {
        ProductItemAdapter{ product ->
            addToSelectedProducts(product)
        }
    }

    private val selectedProductAdapterCategory by lazy {
        SelectedProductItemAdapter {product ->
            removeFromListCategory(product)
        }
    }

    private val selectedProductAdapterGlobal by lazy {
        SelectedProductItemAdapter {product ->
            removeFromListGlobal(product)
        }
    }

    private fun addToSelectedProducts(product: Product){
        productListCategory.add(product)
        productListGlobal.add(product)

        selectedProductAdapterCategory.submitList(productListCategory.toList())
        selectedProductAdapterCategory.notifyItemInserted(productListCategory.size-1)

        selectedProductAdapterGlobal.submitList(productListGlobal.toList())
        selectedProductAdapterGlobal.notifyItemInserted(productListGlobal.size-1)
    }

    private fun removeFromListCategory(product: Product){
        productListCategory.remove(product)
        selectedProductAdapterCategory.submitList(productListCategory.toList())
        selectedProductAdapterCategory.notifyDataSetChanged()
    }

    private fun removeFromListGlobal(product: Product){
        productListGlobal.remove(product)
        selectedProductAdapterGlobal.submitList(productListGlobal.toList())
        selectedProductAdapterGlobal.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsListBinding.inflate(layoutInflater)
        networkListener = NetworkListener()
//        categories = resources.getStringArray(R.array.categories_dropdown_items)
        Log.d("categories", "onCreateView: $categories")

        getNetworkStatus()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        handleCategorySelection(categories[0])
        binding.categoryAutoCompleteTextView.setText(categories[0], false)


        binding.profilePictureImageView.setOnClickListener {
            findNavController().navigate(ProductsListFragmentDirections.actionProductsListFragmentToCustomerIdSelectFragment())
        }

        binding.categoryAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            productListCategory.clear()
            selectedProductAdapterCategory.submitList(productListCategory)
            selectedProductAdapterCategory.notifyDataSetChanged()

            val selectedItem = categories[position]
            handleCategorySelection(selectedItem)
        }

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.productsRecyclerView.adapter = productAdapter


        binding.selectedProductsCategoryRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.selectedProductsCategoryRecyclerView.adapter = selectedProductAdapterCategory

        binding.selectedProductsGlobalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.selectedProductsGlobalRecyclerView.adapter = selectedProductAdapterGlobal


        binding.recommendButton.setOnClickListener {
            val productIdListCategory: List<String> = productListCategory.map { it.product_id }
            val productIdListGlobal: List<String> = productListGlobal.map { it.product_id }

            val gson = Gson()

            val interactionJsonCategory = gson.toJson(productIdListCategory) // Convert list to JSON array string
            val interactionPartCategory = interactionJsonCategory.toRequestBody("application/json".toMediaTypeOrNull())

            val interactionJsonGlobal = gson.toJson(productIdListGlobal) // Convert list to JSON array string
            val interactionPartGlobal = interactionJsonGlobal.toRequestBody("application/json".toMediaTypeOrNull())

            val category = binding.categoryAutoCompleteTextView.text.toString()
            val categoryPart = category.toRequestBody("text/plain".toMediaTypeOrNull())

            val userId = tokenManager.getCustomerId() ?: "32158956"
            val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
//            observeRecommendation()
            lifecycleScope.launch {
                viewModel.getRecommendations(interactionPartCategory, interactionPartGlobal, categoryPart, userIdPart)
            }
            findNavController().navigate(ProductsListFragmentDirections.actionProductsListFragmentToRecommendationFragment())
        }
    }


    private fun handleCategorySelection(category: String){
        binding.categoryAutoCompleteTextView.setText(category, false)
        lifecycleScope.launch {
            viewModel.getProductsByCategory(category)
        }
        observeProducts()
    }

    private fun observeProducts(){
        lifecycleScope.launch {
            viewModel.getProductsResponseStateFlow.collectLatest { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        productAdapter.submitList(response.data?.products_list)
                        productAdapter.notifyDataSetChanged()
                        Log.d("Bookings", "${response.data}")
                    }

                    is NetworkResult.Error<*> -> {
                        Log.d("Bookings_ERROR", "onCreateView: " + response.message)
                        Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                    is NetworkResult.Loading<*> -> {
                        binding.progressCircular.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        categories = resources.getStringArray(R.array.categories_dropdown_items)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding.categoryAutoCompleteTextView.setAdapter(arrayAdapter)

        productListCategory.clear()
        selectedProductAdapterCategory.submitList(productListCategory)
        selectedProductAdapterCategory.notifyDataSetChanged()

        productListGlobal.clear()
        selectedProductAdapterGlobal.submitList(productListGlobal)
        selectedProductAdapterGlobal.notifyDataSetChanged()
    }

    private fun getNetworkStatus() {
        lifecycleScope.launch{
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                Log.d("NetworkListener", "onCreate: $status")
                if (status) {
                    binding.categoryTextLL.visibility = View.VISIBLE
                    binding.nestedScrollView.visibility = View.VISIBLE
                    binding.selectedProductsHeading.visibility = View.VISIBLE
                    binding.selectedProductsGlobalRecyclerView.visibility = View.VISIBLE
                    binding.selectedProductsCategoryRecyclerView.visibility = View.VISIBLE
                    binding.recommendButton.visibility = View.VISIBLE

                    binding.topBar.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                } else {
                    binding.categoryTextLL.visibility = View.INVISIBLE
                    binding.nestedScrollView.visibility = View.INVISIBLE
                    binding.selectedProductsHeading.visibility = View.INVISIBLE
                    binding.selectedProductsGlobalRecyclerView.visibility = View.INVISIBLE
                    binding.selectedProductsCategoryRecyclerView.visibility = View.INVISIBLE
                    binding.recommendButton.visibility = View.INVISIBLE

                    binding.topBar.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.VISIBLE

                    binding.root.background = null
                }

            }
        }
    }
}