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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.majorproject.personalpicks.R
import com.majorproject.personalpicks.data.model.Product
import com.majorproject.personalpicks.databinding.FragmentProductsListBinding
import com.majorproject.personalpicks.domain.adapter.ProductItemAdapter
import com.majorproject.personalpicks.domain.adapter.SelectedProductItemAdapter
import com.majorproject.personalpicks.utils.NetworkListener
import com.majorproject.personalpicks.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsListFragment : Fragment() {

    private val viewModel: ViewModel by viewModels()

    private var _binding: FragmentProductsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var networkListener: NetworkListener
    private val categories: Array<String> = arrayOf("Apparel",
        "Beauty",
        "Giftcard",
        "Jewelry",
        "Watches",
        "Shoes",
        "Luggage",
        "Global")
    private var productList: MutableList<Product> = emptyList<Product>().toMutableList()

    private val productAdapter by lazy {
        ProductItemAdapter{ product ->
            addToSelectedProducts(product)
        }
    }

    private val selectedProductAdapter by lazy {
        SelectedProductItemAdapter {product ->
            removeFromList(product)
        }
    }

    private fun addToSelectedProducts(product: Product){
        productList.add(product)
        selectedProductAdapter.submitList(productList.toList())
        selectedProductAdapter.notifyItemInserted(productList.size-1)
    }

    private fun removeFromList(product: Product){
        productList.remove(product)
        selectedProductAdapter.submitList(productList.toList())
        selectedProductAdapter.notifyDataSetChanged()
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

        handleCategorySelection(categories[0])
        binding.categoryAutoCompleteTextView.setText(categories[0], false)

        binding.categoryAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = categories[position]
            handleCategorySelection(selectedItem)
        }

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.productsRecyclerView.adapter = productAdapter


        binding.selectedProductsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.selectedProductsRecyclerView.adapter = selectedProductAdapter


        binding.recommendButton.setOnClickListener {
            findNavController().navigate(ProductsListFragmentDirections.actionProductsListFragmentToRecommendationFragment())
        }
    }


    private fun handleCategorySelection(category: String){
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
                        productAdapter.submitList(response.data?.products_list)
                        productAdapter.notifyDataSetChanged()
                        Log.d("Bookings", "${response.data}")
                    }

                    is NetworkResult.Error<*> -> {
                        Log.d("Bookings_ERROR", "onCreateView: " + response.message)
                    }

                    is NetworkResult.Loading<*> -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        categories = resources.getStringArray(R.array.categories_dropdown_items)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)
        binding.categoryAutoCompleteTextView.setAdapter(arrayAdapter)
    }

    private fun getNetworkStatus() {
        lifecycleScope.launch{
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                Log.d("NetworkListener", "onCreate: $status")
                if (status) {
                    binding.categoryTextLL.visibility = View.VISIBLE
                    binding.nestedScrollView.visibility = View.VISIBLE
                    binding.selectedProductsHeading.visibility = View.VISIBLE
                    binding.selectedProductsRecyclerView.visibility = View.VISIBLE
                    binding.recommendButton.visibility = View.VISIBLE

                    binding.topBar.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.INVISIBLE
                } else {
                    binding.categoryTextLL.visibility = View.INVISIBLE
                    binding.nestedScrollView.visibility = View.INVISIBLE
                    binding.selectedProductsHeading.visibility = View.INVISIBLE
                    binding.selectedProductsRecyclerView.visibility = View.INVISIBLE
                    binding.recommendButton.visibility = View.INVISIBLE

                    binding.topBar.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.VISIBLE

                    binding.root.background = null
                }

            }
        }
    }
}