@file:Suppress("DEPRECATION")

package com.dea.mentalcare.ui.history

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dea.mentalcare.R
import com.dea.mentalcare.data.entity.HistoryItem
import com.dea.mentalcare.databinding.FragmentHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var itemData: MutableList<HistoryItem>
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataAdapter: DataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage(getString(R.string.show_profile))
        progressDialog.setCancelable(false)

        db = Firebase.firestore
        itemData = mutableListOf()

        recyclerView = binding.rvHistory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage(getString(R.string.show_data))
        dataAdapter = DataAdapter(itemData)
        dataAdapter.setDialog(object : DataAdapter.Dialog {
            override fun onClick(pos: Int) {
                val optionActions = arrayOf<CharSequence>(getString(R.string.delete))
                val dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setItems(optionActions) { _, i ->
                    when (i) {
                        0 -> {
                            deleteData(itemData[pos].id)
                        }
                    }
                }
                dialogBuilder.show()
            }
        })

        FirebaseAuth.getInstance().currentUser?.uid

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = dataAdapter


    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        Log.d(TAG, "getData() called")

        if (isAdded) {
            progressDialog.show()
        }

        usersCollection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                itemData.clear()

                for (documentSnapshot in querySnapshot) {
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: ""
                    val age = documentSnapshot.getString("age") ?: ""
                    val gender = documentSnapshot.getString("gender") ?: ""
                    val status = documentSnapshot.getString("status") ?: ""
                    val maritalStatus = documentSnapshot.getString("maritalStatus") ?: ""
                    val result = documentSnapshot.getString("result") ?: ""
                    val message = documentSnapshot.getString("message") ?: ""

                    val dataItems =
                        HistoryItem(id, name, age, gender, status, maritalStatus, result, message)
                    itemData.add(dataItems)
                }

                dataAdapter.notifyDataSetChanged()
                Log.d(TAG, "Documents retrieved: ${querySnapshot.size()}")
                progressDialog.dismiss()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                progressDialog.dismiss()
            }
    }

    private fun deleteData(id: String) {
        progressDialog.show()
        db.collection("users").document(id)
            .delete()
            .addOnSuccessListener {
                getData()
                progressDialog.dismiss()
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                getData()
                progressDialog.dismiss()
                Log.w(TAG, "Error deleting document", e)
            }
    }
}