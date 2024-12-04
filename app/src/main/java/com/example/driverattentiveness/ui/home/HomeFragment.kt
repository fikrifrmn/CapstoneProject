package com.example.driverattentiveness.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
<<<<<<< HEAD
import android.util.Log
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
<<<<<<< HEAD
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
=======
import com.example.driverattentiveness.R
import com.example.driverattentiveness.databinding.FragmentHomeBinding
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
<<<<<<< HEAD
    private lateinit var adapter: ArticleAdapter
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

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

<<<<<<< HEAD
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
=======
//        binding.logoutButton.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_home_to_navigation_welcome)
//        }
        showBottomNavigation()
        setupView()
//        playAnimation()
        return root
    }
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
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


<<<<<<< HEAD
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

=======
//    private fun playAnimation(){
//        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30F, 30F).apply {
//            duration = 6000 //Mengatur seberapa lama animasi dijalankan.
//            repeatCount = ObjectAnimator.INFINITE // Mengatur jumlah perulangan yang akan dilakukan.
//            repeatMode = ObjectAnimator.REVERSE //Mengatur bagaimana perulangan akan dilakukan, apakah mengulang dari awal atau memutar secara berkebalikan (reverse).
//            startDelay = 100 //Menunda jalannya animasi untuk sekian milisekon ms.
//        }.start()
//
//        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1F).setDuration(300)
//        val tvMessage = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1F).setDuration(300)
//        val btnLogout = ObjectAnimator.ofFloat(binding.logoutButton, View.ALPHA, 1F).setDuration(300)
//
//        val together = AnimatorSet().apply {
//            playTogether(btnLogout)
//        }
//        AnimatorSet().apply {
//            playSequentially( tvName, tvMessage, together)
//            start()
//        }
//    }
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
<<<<<<< HEAD

    companion object {
        private const val TAG = "HomeFragment"
    }
=======
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
}