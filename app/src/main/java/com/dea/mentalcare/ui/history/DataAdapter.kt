package com.dea.mentalcare.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dea.mentalcare.data.entity.HistoryItem
import com.dea.mentalcare.databinding.ItemHistoryBinding

class DataAdapter(private val dataItems: List<HistoryItem>) :
    RecyclerView.Adapter<DataAdapter.AppHolder>() {

    private var dialog: Dialog? = null

    interface Dialog {
        fun onClick(pos: Int)
    }

    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    inner class AppHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                dialog?.onClick(layoutPosition)
            }
        }

        fun bind(item: HistoryItem) {
            binding.apply {
                tvName.text = item.name
                tvAge.text = item.age
                tvStatus.text = item.status
                tvMaritalStatus.text = item.maritalStatus
                tvResult.text = item.result
                tvMsgInput.text = item.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppHolder(binding)
    }

    override fun onBindViewHolder(holder: AppHolder, position: Int) {
        val item = dataItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataItems.size
    }
}