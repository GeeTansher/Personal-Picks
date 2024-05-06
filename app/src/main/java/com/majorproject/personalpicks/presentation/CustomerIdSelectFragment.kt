package com.majorproject.personalpicks.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.majorproject.personalpicks.R
import com.majorproject.personalpicks.ViewModel
import com.majorproject.personalpicks.databinding.FragmentCustomerIdSelectBinding
import com.majorproject.personalpicks.domain.adapter.CustomerIdItemAdapter
import com.majorproject.personalpicks.utils.NetworkResult
import com.majorproject.personalpicks.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CustomerIdSelectFragment : Fragment() {

    private var _binding: FragmentCustomerIdSelectBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ViewModel

    @Inject
    lateinit var tokenManager: TokenManager

    private val customerIdAdapter by lazy {
        CustomerIdItemAdapter{ id ->
            saveId(id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomerIdSelectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        binding.selectedCustomerTextView.text = tokenManager.getCustomerId()
        binding.selectedCustomerRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.selectedCustomerRecyclerView.adapter = customerIdAdapter

        lifecycleScope.launch {
            viewModel.getCustomerIds()
        }
        observeProducts()

    }


    private fun observeProducts(){
        lifecycleScope.launch {
            viewModel.getCustomerIdResponseStateFlow.collectLatest { response ->
                when (response) {
                    is NetworkResult.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        customerIdAdapter.submitList(response.data?.id_list)
                        customerIdAdapter.notifyDataSetChanged()
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

    private fun saveId(id: String){
        tokenManager.saveCustomerId(id)
        binding.selectedCustomerTextView.text = id
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}