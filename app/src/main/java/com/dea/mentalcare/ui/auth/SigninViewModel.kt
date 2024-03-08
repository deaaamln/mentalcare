package com.dea.mentalcare.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dea.mentalcare.utils.Event
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _navigateToHome = MutableLiveData<Event<Unit>>()
    val navigateToHome: LiveData<Event<Unit>> get() = _navigateToHome

    private val _showToast = MutableLiveData<Event<String>>()
    val showToast: LiveData<Event<String>> get() = _showToast

    fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _navigateToHome.value = Event(Unit)
                _showToast.value =
                    Event("SignIn was successful! Welcome to the home page")
            }
            .addOnFailureListener { error ->
                _showToast.value = Event(error.localizedMessage ?: "An error occurred")
            }
    }
}