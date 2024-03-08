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
import androidx.activity.viewModels
import com.dea.mentalcare.R
import com.dea.mentalcare.databinding.ActivitySignInBinding
import com.dea.mentalcare.ui.homepage.HomeActivity
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.apply {
            title = getString(R.string.signin)
            setDisplayHomeAsUpEnabled(true)
        }

        playAnimation()
        setupObservers()
        setupAction()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewSignIn, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val edEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val edPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signIn = ObjectAnimator.ofFloat(binding.signinButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(tvEmail, edEmail, tvPassword, edPassword, signIn)
            start()
        }
    }

    private fun setupObservers() {
        signInViewModel.navigateToHome.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                showLoading(false)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        signInViewModel.showToast.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                showLoading(false)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            if (emailEditText.length() == 0 && passwordEditText.length() == 0) {
                emailEditText.error = getString(R.string.email)
                passwordEditText.setError(getString(R.string.password), null)
                signinButton.isEnabled = false
            } else if (emailEditText.length() != 0 && passwordEditText.length() != 0) {
                signinButton.isEnabled = true
            }

            emailEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    signinButton.isEnabled =
                        emailEditText.text!!.isNotEmpty() && passwordEditText.text!!.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    signinButton.isEnabled = passwordEditText.length() >= 8
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            signinButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                showLoading(true)

                signInViewModel.signIn(email, password)
            }
        }
    }

    private fun showLoading(b: Boolean) {
        binding.pbSignin.visibility = if (b) View.VISIBLE else View.GONE
        binding.signinButton.visibility = if (b) View.GONE else View.VISIBLE
    }
}