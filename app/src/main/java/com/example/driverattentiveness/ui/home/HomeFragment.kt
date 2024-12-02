package com.example.driverattentiveness.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.driverattentiveness.R
import com.example.driverattentiveness.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel

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

//        binding.logoutButton.setOnClickListener {
//            findNavController().navigate(R.id.action_navigation_home_to_navigation_welcome)
//        }
        showBottomNavigation()
        setupView()
//        playAnimation()
        return root
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}