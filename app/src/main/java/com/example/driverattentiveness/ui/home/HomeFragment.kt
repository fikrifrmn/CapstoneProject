package com.example.driverattentiveness.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driverattentiveness.BuildConfig
import com.example.driverattentiveness.R
import com.example.driverattentiveness.data.adapter.ArticleAdapter
import com.example.driverattentiveness.data.api.response.ArticleResponse
import com.example.driverattentiveness.data.api.response.ArticlesItem
import com.example.driverattentiveness.data.api.retrofit.ApiArticleConfig
import com.example.driverattentiveness.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: ArticleAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupView()
        setupRecyclerView()
        findDataArticle()

        return root
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }
    private fun showBottomNavigation() {
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
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


    private fun findDataArticle() {
        showLoading()
        val apiKey = BuildConfig.API_KEY
        ApiArticleConfig.getApiService().getData(apiKey = apiKey)
            .enqueue(object : Callback<ArticleResponse> {
                override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                    hideLoading()
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val validArticles = responseBody?.articles?.filter {
                            it?.title != "[Removed]" && it?.description != "[Removed]" && it?.content != "[Removed]"
                        }

                        if (!validArticles.isNullOrEmpty()) {
                            setDataArticles(validArticles)
                        } else {
                            Log.e(TAG, "No valid articles found.")
                        }
                    } else {
                        Log.e(TAG, "onResponse failed: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                    hideLoading()
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
    }


    private fun setDataArticles(dataArticles: List<ArticlesItem?>) {
        Log.d(TAG, "Number of articles: ${dataArticles.size}")
        adapter.submitList(dataArticles)
    }

    private fun showLoading() {
        binding.progressBar.visibility = android.view.View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = android.view.View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}