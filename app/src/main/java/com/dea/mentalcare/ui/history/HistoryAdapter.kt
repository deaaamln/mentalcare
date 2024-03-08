package com.dea.mentalcare.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dea.mentalcare.R
import com.dea.mentalcare.data.entity.HistoryItem

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList = mutableListOf<HistoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = historyList[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(historyItem: HistoryItem) {
            itemView.apply {
                findViewById<TextView>(R.id.tv_name).text = historyItem.name
                findViewById<TextView>(R.id.tv_age).text = historyItem.age
                findViewById<TextView>(R.id.tv_status).text = historyItem.status
                findViewById<TextView>(R.id.tv_maritalStatus).text = historyItem.maritalStatus
                findViewById<TextView>(R.id.tv_result).text = historyItem.result
                findViewById<TextView>(R.id.tv_message).text = historyItem.message
            }
        }
    }
}
