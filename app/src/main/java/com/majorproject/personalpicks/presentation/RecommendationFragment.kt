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
import androidx.recyclerview.widget.LinearLayoutManager
import com.majorproject.personalpicks.R
import com.majorproject.personalpicks.ViewModel
import com.majorproject.personalpicks.databinding.FragmentRecommendationBinding
import com.majorproject.personalpicks.domain.adapter.ProductItemAdapter
import com.majorproject.personalpicks.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendationFragment : Fragment() {
//    private val viewModel: ViewModel by viewModels()

    private lateinit var viewModel: ViewModel

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    private val categories: Array<String> = arrayOf("Recommendation based of category",
        "Global recommendation")

    private val productAdapter by lazy {
        ProductItemAdapter{ _ ->
//            addToSelectedProducts(product)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(layoutInflater)
//        observeRecommendation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]
        observeRecommendation()
//        Log.d("recommend111", "${viewModel.globalList}")

        binding.recommendationTypeAutoCompleteTextView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            val selectedItem = categories[position]
            handleCategorySelection(position)
        }

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.productsRecyclerView.adapter = productAdapter

        handleCategorySelection(1)

    }

    private fun handleCategorySelection(category: Int){
        if(category == 0){
            productAdapter.submitList(viewModel.categoryList)
            productAdapter.notifyDataSetChanged()
            binding.recommendationTypeAutoCompleteTextView.setText(categories[0], false)
        }
        else{
            productAdapter.submitList(viewModel.globalList)
            productAdapter.notifyDataSetChanged()
            binding.recommendationTypeAutoCompleteTextView.setText(categories[1], false)
        }
    }

    private fun observeRecommendation(){
        lifecycleScope.launch {
            viewModel.getRecommendationResponseStateFlow.collectLatest { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        viewModel.globalList = response.data?.globalProductIds!!
                        viewModel.categoryList = response.data.categoryProductIds
                        handleCategorySelection(1)
                        Log.d("recommend333", "${response.data}")
                        Log.d("recommend222", "${viewModel.globalList}")
                    }

                    is NetworkResult.Error<*> -> {
                        Log.d("recommend_ERROR", "onCreateView: " + response.message)
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
        binding.recommendationTypeAutoCompleteTextView.setAdapter(arrayAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}