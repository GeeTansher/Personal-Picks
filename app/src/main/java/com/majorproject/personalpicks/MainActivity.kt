package com.majorproject.personalpicks

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.majorproject.personalpicks.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        viewModel.categoryList = emptyList()
        viewModel.globalList = emptyList()

//        binding.apply {
//            val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment_container_view)as NavHostFragment
////            val navController = navHostFragment.navController
//        }
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        window?.statusBarColor = ContextCompat.getColor(this, R.color.lightGray)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}