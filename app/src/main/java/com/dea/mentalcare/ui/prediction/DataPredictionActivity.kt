package com.dea.mentalcare.ui.prediction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dea.mentalcare.R
import com.dea.mentalcare.data.entity.DemographicData
import com.dea.mentalcare.databinding.ActivityDataPrediction2Binding
import com.dea.mentalcare.ui.result.ResultPredictionActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

@Suppress("DEPRECATION")
class DataPredictionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataPrediction2Binding
    private var previousSelections = mutableMapOf<Int, Boolean>()
    private var totalInputValue = 0
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataPrediction2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.apply {
            title = getString(R.string.prediction_data)
            setDisplayHomeAsUpEnabled(true)
        }

        buttonGroup()

        interpreter = Interpreter(loadModelByteBuffer("model.tflite"))

        binding.doneButton.setOnClickListener {
            val demographicData = intent.getParcelableExtra<DemographicData>("demographicData")
            if (demographicData != null) {
                uploadData(demographicData)
                runModelInference()
                val intent = Intent(this, ResultPredictionActivity::class.java)
                intent.putExtra("resultValue", totalInputValue)
                startActivity(intent)
            } else {
                Toast.makeText(this,
                    getString(R.string.demographic_data_not_available), Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun uploadData(demographicData: DemographicData) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("UserId", "uploadData: $userId ")

        val user = hashMapOf(
            "userId" to userId,
            "name" to demographicData.name,
            "age" to demographicData.age,
            "gender" to demographicData.gender,
            "status" to demographicData.status,
            "maritalStatus" to demographicData.maritalStatus,
            "result" to totalInputValue.toString(),
            "message" to ""
        )

        if (userId != null) {
            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    updateDocumentInFirestore(it.id)
                    Log.d("DataUpload", "Data successfully uploaded to Firestore")
                    Toast.makeText(this,
                        getString(R.string.data_successfully_updated), Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ResultPredictionActivity::class.java)
                    intent.putExtra("resultValue", totalInputValue)
                    intent.putExtra("docId", it.id)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.w("DataUpload", "Error when uploading data", e)
                    Toast.makeText(this,
                        getString(R.string.data_not_successfully_updated), Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("DataUpload", "UserID is empty or no user is logged in")
        }
    }

    private fun buttonGroup(){
        binding.apply {
            setRadioButtonListener(radioGroup1, yesRadioButton1)
            setRadioButtonListener(radioGroup2, yesRadioButton2)
            setRadioButtonListener(radioGroup3, yesRadioButton3)
            setRadioButtonListener(radioGroup4, yesRadioButton4)
            setRadioButtonListener(radioGroup5, yesRadioButton5)
            setRadioButtonListener(radioGroup6, yesRadioButton6)
            setRadioButtonListener(radioGroup7, yesRadioButton7)
            setRadioButtonListener(radioGroup8, yesRadioButton8)
            setRadioButtonListener(radioGroup9, yesRadioButton9)
            setRadioButtonListener(radioGroup10, yesRadioButton10)
            setRadioButtonListener(radioGroup11, yesRadioButton11)
            setRadioButtonListener(radioGroup12, yesRadioButton12)
            setRadioButtonListener(radioGroup13, yesRadioButton13)
            setRadioButtonListener(radioGroup14, yesRadioButton14)
            setRadioButtonListener(radioGroup15, yesRadioButton15)
            setRadioButtonListener(radioGroup16, yesRadioButton16)
            setRadioButtonListener(radioGroup17, yesRadioButton17)
            setRadioButtonListener(radioGroup18, yesRadioButton18)
            setRadioButtonListener(radioGroup19, yesRadioButton19)
            setRadioButtonListener(radioGroup20, yesRadioButton20)
        }
    }

    private fun setRadioButtonListener(radioGroup: RadioGroup, radioButton: RadioButton) {
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val isChecked = checkedId == radioButton.id

            val previousSelection = previousSelections[radioGroup.id] ?: false

            if (isChecked && !previousSelection) {
                totalInputValue++
                Log.d("TotalInputValue", "Value: $totalInputValue")
                resetPreviousSelections(radioGroup.id)
            } else if (!isChecked && previousSelection) {
                totalInputValue--
                Log.d("TotalInputValue", "Value: $totalInputValue")
            }

            previousSelections[radioGroup.id] = isChecked
        }
    }

    private fun loadModelByteBuffer(modelFileName: String): ByteBuffer {
        val assetManager = assets
        val assetFileDescriptor = assetManager.openFd(modelFileName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun runModelInference(): Int {
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 29), DataType.FLOAT32)
        val outputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 2), DataType.FLOAT32)
        interpreter.run(inputFeature0.buffer, outputFeature0.buffer)

        return outputFeature0.intArray[0]
    }

    private fun updateDocumentInFirestore(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val franchisesCollection = db.collection("users")

        val updatedData = mapOf(
            "documentId" to documentId
        )

        // Lakukan pembaruan data di Firestore
        franchisesCollection.document(documentId)
            .update(updatedData)
    }

    private fun resetPreviousSelections(currentGroupId: Int) {
        for ((groupId) in previousSelections) {
            if (groupId != currentGroupId) {
                previousSelections[groupId] = false
            }
        }
    }
}
