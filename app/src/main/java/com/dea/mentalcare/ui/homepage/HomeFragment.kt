package com.dea.mentalcare.ui.homepage

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dea.mentalcare.R
import com.dea.mentalcare.databinding.FragmentHomeBinding
import com.dea.mentalcare.ui.auth.SignInActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val imageUrl = viewModel.getImageUrl()
            Glide.with(this@HomeFragment)
                .load(imageUrl)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error)
                )
                .into(imgPoster)

            imgPoster.setOnClickListener {
                viewModel.openWebsite(requireContext())
            }

            if (viewModel.isUserLoggedIn()) {
                nameTextView.text = viewModel.getUserName()
            } else {
                startActivity(Intent(requireContext(), SignInActivity::class.java))
                requireActivity().finish()
            }

            setupEmotionButtonListeners()
            setupStartButtonListener()
            setupCardClickListeners()
        }
    }

    private fun setupEmotionButtonListeners() {
        binding.apply {
            angry.setOnClickListener {
                viewModel.showAlert(
                    requireContext(),
                    getString(R.string.angryText),
                    getString(R.string.angry_text)
                )
            }

            sad.setOnClickListener {
                viewModel.showAlert(
                    requireContext(),
                    getString(R.string.sadText),
                    getString(R.string.sad_text)
                )
            }

            neutral.setOnClickListener {
                viewModel.showAlert(
                    requireContext(),
                    getString(R.string.neutralText),
                    getString(R.string.neutral_text)
                )
            }

            happy.setOnClickListener {
                viewModel.showAlert(
                    requireContext(),
                    getString(R.string.happyText),
                    getString(R.string.happy_text)
                )
            }
        }
    }

    private fun setupStartButtonListener() {
        binding.startButton.setOnClickListener {
            viewModel.startDataDemografisActivity(requireContext())
        }
    }

    private fun setupCardClickListeners() {
        val counselingCard = binding.cvCounseling
        val meditationCard = binding.cvMeditation

        counselingCard.setOnClickListener {
            viewModel.handleCounselingCardClick(requireContext())
        }

        meditationCard.setOnClickListener {
            viewModel.handleMeditationCardClick(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
