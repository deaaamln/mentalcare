package com.dea.mentalcare.ui.prediction

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.dea.mentalcare.R
import com.dea.mentalcare.data.entity.DemographicData
import com.dea.mentalcare.databinding.ActivityDataDemografisBinding

@Suppress("DEPRECATION")
class DemographicsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataDemografisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataDemografisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.apply {
            title = getString(R.string.demographic_data)
            setDisplayHomeAsUpEnabled(true)
        }

        val genderOptions = arrayOf("Male", "Female", "Others")
        val statusOptions = arrayOf("Employed", "Unemployed")
        val maritalStatusOptions = arrayOf("Single", "Married", "Divorced")

        val genderAdapter = ArrayAdapter(this, R.layout.text_type_item, genderOptions)
        val statusAdapter = ArrayAdapter(this, R.layout.text_type_item, statusOptions)
        val maritalStatusAdapter =
            ArrayAdapter(this, R.layout.text_type_item, maritalStatusOptions)

        binding.autoCompleteTextView.setAdapter(genderAdapter)
        binding.autoCompleteTextView2.setAdapter(statusAdapter)
        binding.autoCompleteTextView3.setAdapter(maritalStatusAdapter)

        binding.btnNext.setOnClickListener {
            sendDataDemographics()
        }
        playAnimation()
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

    private fun sendDataDemographics() {
        val name = binding.nameEditText.text.toString()
        val age = binding.ageEditText.text.toString()
        val gender = binding.autoCompleteTextView.text.toString()
        val employmentStatus = binding.autoCompleteTextView2.text.toString()
        val maritalStatus = binding.autoCompleteTextView3.text.toString()

        val demographicData = DemographicData(name, age, gender, employmentStatus, maritalStatus)

        val intent = Intent(this, DataPredictionActivity::class.java)
        intent.putExtra("demographicData", demographicData)
        Log.d("DataDebug", "Demographic Data: $demographicData")

        startActivity(intent)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageDemo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}