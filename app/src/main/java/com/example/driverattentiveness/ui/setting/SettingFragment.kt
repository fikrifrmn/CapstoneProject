package com.example.driverattentiveness.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.driverattentiveness.ViewModelFactory
import com.example.driverattentiveness.databinding.FragmentSettingBinding
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel menggunakan ViewModelFactory
        val factory = ViewModelFactory.getInstance(requireContext())
        settingViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]

        settingViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.edtName.setText(user.name)
        }

        binding.btnSave.setOnClickListener {
            val newPassword = binding.edtPassword.text.toString()

            if (newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                settingViewModel.changePassword(newPassword) { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                        view.findNavController().navigateUp()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
