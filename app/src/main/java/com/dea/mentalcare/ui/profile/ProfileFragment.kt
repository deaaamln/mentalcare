package com.dea.mentalcare.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dea.mentalcare.R
import com.dea.mentalcare.databinding.FragmentProfileBinding
import com.dea.mentalcare.ui.welcome.WelcomeActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (displayName, email) = viewModel.getUserInfo()

        if (!displayName.isNullOrBlank()) {
            binding.nameTextView.text = displayName
        }

        if (!email.isNullOrBlank()) {
            binding.emailTextView.text = email
        }

        binding.tvLanguage.setOnClickListener {
            val languageIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(languageIntent)
        }

        binding.tvLeave.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
