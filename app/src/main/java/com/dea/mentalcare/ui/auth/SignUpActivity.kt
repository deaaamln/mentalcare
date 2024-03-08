package com.dea.mentalcare.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.dea.mentalcare.R
import com.dea.mentalcare.databinding.ActivitySignUpBinding
import androidx.activity.viewModels
import com.dea.mentalcare.ui.homepage.HomeActivity
import com.dea.mentalcare.ui.welcome.WelcomeActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignupViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.navigateToSignIn.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.apply {
            title = getString(R.string.signup)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.signinButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        playAnimation()
        setupAction()
        observeViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewSignUp, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val edName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val edEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val edPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signUp = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)
        val signIn = ObjectAnimator.ofFloat(binding.signinButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                tvName,
                edName,
                tvEmail,
                edEmail,
                tvPassword,
                edPassword,
                signUp,
                signIn
            )
            start()
        }
    }

    private fun observeViewModel() {
        viewModel.showToast.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showLoading(false)

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            val isNameEmpty = nameEditText.text.isNullOrEmpty()
            val isEmailEmpty = emailEditText.text.isNullOrEmpty()
            val isPasswordEmpty = passwordEditText.text.isNullOrEmpty()

            if (isNameEmpty || isEmailEmpty || isPasswordEmpty) {
                nameEditText.error = if (isNameEmpty) getString(R.string.error_name) else null
                emailEditText.error = if (isEmailEmpty) getString(R.string.invalidEmail) else null
                passwordEditText.error =
                    if (isPasswordEmpty) getString(R.string.error_password) else null
                signupButton.isEnabled = false
            } else {
                signupButton.isEnabled = true
            }
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.signupButton.isEnabled =
                    binding.emailEditText.text!!.isNotEmpty() && binding.passwordEditText.text!!.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.signupButton.isEnabled = binding.passwordEditText.length() >= 8
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            showLoading(true)

            viewModel.signUp(name, email, password)
        }
    }

    private fun showLoading(b: Boolean) {
        binding.pbSignup.visibility = if (b) View.VISIBLE else View.GONE
        binding.signupButton.visibility = if (b) View.GONE else View.VISIBLE
    }
}