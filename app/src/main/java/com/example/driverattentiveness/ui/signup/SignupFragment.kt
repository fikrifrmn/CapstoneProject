package com.example.driverattentiveness.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.driverattentiveness.R
import com.example.driverattentiveness.databinding.FragmentSignupBinding

class SignupFragment: Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private lateinit var signupBinding: FragmentSignupBinding
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
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
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            AlertDialog.Builder(requireContext()).apply {
                setTitle("Yeah!")
                setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
                setPositiveButton("Lanjut") { _, _ ->
                    // Navigasi kembali ke WelcomeFragment
                    val navController = findNavController()
                    navController.navigate(R.id.action_navigation_signup_to_navigation_welcome)
                }
                create()
                show()
            }
        }
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            startDelay = 100
        }.start()

        val tvTitle = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1F).setDuration(300)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1F).setDuration(300)
        val layoutName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1F).setDuration(300)
        val layoutEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1F).setDuration(300)
        val layoutPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val btnSignup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1F).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(btnSignup)
        }
        AnimatorSet().apply {
            playSequentially(tvTitle, tvName,layoutName, tvEmail, layoutEmail, tvPassword, layoutPassword, together)
            start()
        }

    }
}