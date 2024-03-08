package com.dea.mentalcare.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.dea.mentalcare.utils.Event

class SignupViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _navigateToSignIn = MutableLiveData<Event<Unit>>()
    val navigateToSignIn: LiveData<Event<Unit>> get() = _navigateToSignIn

    private val _showToast = MutableLiveData<Event<String>>()
    val showToast: LiveData<Event<String>> get() = _showToast

    fun signUp(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userUpdateProfile = firebaseAuth.currentUser?.let {
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    }
                    val user = task.result?.user
                    if (userUpdateProfile != null) {
                        user?.updateProfile(userUpdateProfile)
                            ?.addOnSuccessListener {
                                _navigateToSignIn.value = Event(Unit)
                                _showToast.value =
                                    Event("Signup was successful! Welcome to the home page")
                            }
                            ?.addOnFailureListener { error ->
                                Event(error.localizedMessage).also {
                                    _showToast.value =
                                        it
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { error ->
                (Event(error.localizedMessage) as Event<String>?).also { _showToast.value = it }
            }
    }
}