package com.dea.mentalcare.ui.result

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dea.mentalcare.R
import com.dea.mentalcare.databinding.ActivityResultPredictionBinding
import com.dea.mentalcare.ui.homepage.HomeActivity
import com.google.firebase.firestore.FirebaseFirestore
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class ResultPredictionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultPredictionBinding
    private lateinit var interpreter: Interpreter
    private val numClasses = 2
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var docId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar?.apply {
            title = getString(R.string.prediction)
        }

        val resultValue = intent.getIntExtra("resultValue", 0)
        docId = intent.getStringExtra("docId")

        val modelByteBuffer = loadModelByteBuffer("model.tflite")

        interpreter = Interpreter(modelByteBuffer)

        val result = runModelInference()
        displayResult(resultValue)

        docId?.let { updateData(it, binding.tvMessage.text.toString()) }

        binding.backToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadModelByteBuffer(modelFileName: String): ByteBuffer {
        val assetManager: AssetManager = assets
        val assetFileDescriptor: AssetFileDescriptor = assetManager.openFd(modelFileName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun runModelInference(): Int {
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 29), DataType.FLOAT32)
        val outputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, numClasses), DataType.FLOAT32)

        interpreter.run(inputFeature0.buffer, outputFeature0.buffer)

        return outputFeature0.floatArray[0].toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun displayResult(resultValue: Int) {

        if (resultValue <= 10) {
            binding.tvAdditionalMessage.text = getString(R.string.low_probability)
        } else {
            binding.tvAdditionalMessage.text = getString(R.string.high_probability)
        }

        when(resultValue) {
            in 0..6 -> {
                binding.ivFeel.setImageResource(R.drawable.happy)
            }
            in 7..13-> {
                binding.ivFeel.setImageResource(R.drawable.neutral)
            }
            in 14..20 -> {
                binding.ivFeel.setImageResource(R.drawable.sad)
            }
        }

        when (resultValue) {
            in 1..2 -> {
                binding.tvMessage.text = getString(R.string.low_probability_message1)
            }
            in 3..4 -> {
                binding.tvMessage.text = getString(R.string.low_probability_message2)
            }
            in 5..6 -> {
                binding.tvMessage.text = getString(R.string.low_probability_message3)
            }
            in 7..8 -> {
                binding.tvMessage.text = getString(R.string.low_probability_message4)
            }
            in 9..10 -> {
                binding.tvMessage.text = getString(R.string.low_probability_message5)
            }
            in 11..12 -> {
                binding.tvMessage.text = getString(R.string.high_probability_message1)
            }
            in 13..14 -> {
                binding.tvMessage.text = getString(R.string.high_probability_message2)
            }
            in 15..16 -> {
                binding.tvMessage.text = getString(R.string.high_probability_message3)
            }
            in 17..18 -> {
                binding.tvMessage.text = getString(R.string.high_probability_message4)
            }
            in 19..20 -> {
                binding.tvMessage.text = getString(R.string.high_probability_message5)
            }
        }
    }

    private fun updateData(userId: String, newResultValue: String) {

        Log.d("UserId", "uploadData: $userId ")
        Log.d("newValue", "uploadData: $newResultValue ")
        val userRef = db.collection("users").document(docId!!)

        userRef
            .update("message", newResultValue)
            .addOnSuccessListener {
                Log.d("UpdateData", "Data berhasil diperbarui di Firestore")

            }
            .addOnFailureListener { e ->
                Log.w("UpdateData", "Error saat memperbarui data", e)
            }
    }

    override fun onDestroy() {
        interpreter.close()
        super.onDestroy()
    }
}