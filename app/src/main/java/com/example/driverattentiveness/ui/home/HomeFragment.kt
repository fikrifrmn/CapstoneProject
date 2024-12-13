package com.example.driverattentiveness.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driverattentiveness.data.adapter.ArticleAdapter
import com.example.driverattentiveness.R
import com.example.driverattentiveness.ViewModelFactory
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.retrofit.ApiArticleConfig
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.database.DatabaseHelper
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import com.example.driverattentiveness.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: ArticleAdapter
    private val binding get() = _binding!!
    private lateinit var  homeViewModel: HomeViewModel
    private lateinit var tvKilometerHome: TextView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userRepository: UserRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[HomeViewModel::class.java]

        setupView()
        observeAlertCount()
        setupRecyclerView()
        observeViewModel()
        showBottomNavigation()

        homeViewModel.fetchArticles()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvKilometerHome = binding.tvKilometerHome

        userRepository = UserRepository.getInstance(UserPreference.getInstance(requireContext().dataStore), ApiConfig.getApiService(""))
        databaseHelper = DatabaseHelper(requireContext())


        lifecycleScope.launch {
            val user = userRepository.getSession().first()
            val totalDistance = databaseHelper.getTotalDistance(user.id)
            tvKilometerHome.text = "$totalDistance km"// Get total distance based on user ID
            Log.d("HomeFragment", "Total Distance: $totalDistance km")
            // Update UI with totalDistance
        }
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = this@HomeFragment.adapter
        }
    }

    private fun showBottomNavigation() {
        requireActivity().findViewById<View>(R.id.navigation_container)?.visibility = View.VISIBLE
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    private fun observeViewModel() {

        homeViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null && user.isLogin) {
                val formattedName = user.name.split(" ").joinToString(" ") { it.capitalize(Locale.ROOT) } ?: ""
                binding.tvTitleHome.text = getString(R.string.greeting, formattedName)
            } else {
                binding.tvTitleHome.text = getString(R.string.guest_greeting)
            }
        }

        // Observe loading state
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Observe article list
        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            if (!articles.isNullOrEmpty()) {
                adapter.submitList(articles)
            } else {
                Log.e(TAG, "No valid articles found.")
            }
        }
    }

    private fun observeAlertCount() {
        val userPreference = UserPreference.getInstance(requireContext().dataStore)

        // Launch coroutine to get the alert count
        lifecycleScope.launch {
            val alertCount = userPreference.getAlertCount().first() // Get alert count from preferences
            binding.tvAlertScoreHome.text = "$alertCount" // Display it in the TextView
            Log.d(TAG, "Alert Count: $alertCount") // For debug purpose
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
