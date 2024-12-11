package com.example.driverattentiveness.ui.welcome

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
import com.example.driverattentiveness.databinding.FragmentWelcomeBinding

class WelcomeFragment: Fragment() {

    private var _binding: FragmentWelcomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupView()
        setupAction()
        playAnimation()
        hideBottomNavigation()

        return root
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

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_welcome_to_navigation_login)
        }

        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_welcome_to_navigation_signup)
        }
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val btnLogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)
        val btnSignup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(300)
        val tvTitle = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val tvDesc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(300)

        //menggabungkan animasi dan mengatur bagaimana alur animasi yang akan muncul di layar
        val together = AnimatorSet().apply {
            playTogether(btnLogin, btnSignup)
        }

        AnimatorSet().apply {
            playSequentially(tvTitle, tvDesc, together)
            start()
        }
    }
}