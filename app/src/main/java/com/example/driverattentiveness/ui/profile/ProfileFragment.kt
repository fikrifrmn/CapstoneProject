package com.example.driverattentiveness.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.driverattentiveness.R
import com.example.driverattentiveness.ViewModelFactory
import com.example.driverattentiveness.data.UserRepository
import com.example.driverattentiveness.data.api.retrofit.ApiConfig
import com.example.driverattentiveness.data.pref.UserPreference
import com.example.driverattentiveness.data.pref.dataStore
import com.example.driverattentiveness.databinding.FragmentProfileBinding
import com.example.driverattentiveness.ui.welcome.WelcomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val apiService = ApiConfig.getApiService("") // Token kosong karena kita akan logout
        userRepository = UserRepository.getInstance(userPreference, apiService)
        binding.btnLogout.setOnClickListener {
            logout()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.userSession.observe(viewLifecycleOwner) { user ->
            Log.d("ProfileFragment", "User name: ${user.name}, email: ${user.email}")
            binding.tvProfileName.text = user.name
            binding.tvProfileEmail.text = user.email
            binding.tvName.text = user.name
        }

        binding.btnEditProfile.setOnClickListener{
            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.navigation_setting)
        }
    }

    private fun logout() {
        // Lakukan logout
        CoroutineScope(Dispatchers.Main).launch {
            userRepository.logout()

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)

            // Membersihkan back stack
            navController.popBackStack(R.id.navigation_profile, true)

            // Navigasi ke WelcomeFragment
            navController.navigate(R.id.navigation_welcome)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}