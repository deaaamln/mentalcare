package com.dea.mentalcare.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel: ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun getUserInfo(): Pair<String?, String?> {
        val firebaseUser = firebaseAuth.currentUser
        return Pair(firebaseUser?.displayName, firebaseUser?.email)
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}