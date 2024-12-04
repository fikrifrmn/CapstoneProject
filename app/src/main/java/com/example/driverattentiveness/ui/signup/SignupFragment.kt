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
<<<<<<< HEAD
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.driverattentiveness.R
import com.example.driverattentiveness.ViewModelFactory.Companion.getInstance
import com.example.driverattentiveness.data.api.response.RegisterResponse
import com.example.driverattentiveness.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val signupViewModel: SignupViewModel by activityViewModels {
        getInstance(requireContext())
    }
=======
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

>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupView()
        setupAction()
<<<<<<< HEAD
=======
        playAnimation()
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
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

<<<<<<< HEAD
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val age = binding.ageEditText.text.toString()

            showLoading(true)
            signupViewModel.register(name, email, password, age, onSuccess = { response ->
                showLoading(false)
                handleSignupResponse(response)
            }, onError = { errorMessage ->
                showLoading(false)
                showToast(errorMessage)
            })
        }
    }
    private fun handleSignupResponse(response: RegisterResponse) {
        if (response.error.isNullOrEmpty()) {
            showDialogSuccess()
        } else {
            showToast(response.error)
        }
    }

    private fun showDialogSuccess() {
        val email = binding.emailEditText.text.toString()
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Yeah!")
            setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
            setPositiveButton("Lanjut") { _, _ ->
                val navController = findNavController()
                navController.navigate(R.id.action_navigation_signup_to_navigation_welcome)
            }
            create()
            show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }
}
=======
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
>>>>>>> d31399869ce56d62f7db37226c5130d473ae5d61
