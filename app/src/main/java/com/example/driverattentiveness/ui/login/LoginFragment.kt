package com.example.driverattentiveness.ui.login

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.driverattentiveness.R
import com.example.driverattentiveness.ViewModelFactory
import com.example.driverattentiveness.data.pref.UserModel
import com.example.driverattentiveness.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private lateinit var loginViewModel: LoginViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val loginViewModel =
//            ViewModelProvider(this)[LoginViewModel::class.java]

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Menggunakan ViewModelFactory untuk mendapatkan instance LoginViewModel
        loginViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireContext())).get(LoginViewModel::class.java)

        setupView()
        setupAction()
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
            val email = binding.emailEditText.text.toString()
            loginViewModel.saveSession(UserModel(email, "sample_token"))
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Yeah!")
                setMessage("Anda berhasil login. Sudah tidak sabar untuk belajar ya?")
                setPositiveButton("Lanjut") { _, _ ->
                    // Navigasi ke HomeFragment
                    val navController = findNavController()
                    navController.navigate(R.id.action_navigation_login_to_navigation_home)
                }
                create()
                show()
            }
        }
    }

    private fun hideBottomNavigation() {
        requireActivity().findViewById<View>(R.id.bottom_navigation_view)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}