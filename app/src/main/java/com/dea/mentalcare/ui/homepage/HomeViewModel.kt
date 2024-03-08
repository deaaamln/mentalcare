package com.dea.mentalcare.ui.homepage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dea.mentalcare.R
import com.dea.mentalcare.ui.prediction.DemographicsActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun getImageUrl(): String {
        return "https://www.selecthub.com/wp-content/uploads/2023/03/Facts-About-Mental-Health-Cover.jpg"
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getUserName(): String? {
        return firebaseAuth.currentUser?.displayName
    }

    fun openWebsite(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(context.getString(R.string.website_url))
        context.startActivity(intent)
    }

    fun showAlert(context: Context, title: String, message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle(title)
            alertDialogBuilder.setMessage(message)
            alertDialogBuilder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }
    }

    fun startDataDemografisActivity(context: Context) {
        val intent = Intent(context, DemographicsActivity::class.java)
        context.startActivity(intent)
    }

    private fun showToast(context: Context, message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun handleCounselingCardClick(context: Context) {
        showToast(context, context.getString(R.string.counseling_toast))
    }

    fun handleMeditationCardClick(context: Context) {
        showToast(context, context.getString(R.string.meditation_toast))
    }
}