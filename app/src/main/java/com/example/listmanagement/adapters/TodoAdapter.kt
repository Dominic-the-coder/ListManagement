package com.example.listmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.listmanagement.R
import com.example.listmanagement.model.TodoDetails

class TodoAdapter(private val todoDetails: List<TodoDetails>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val detailsTextView: TextView = itemView.findViewById(R.id.textViewDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val TodoDetails = todoDetails[position]
        holder.titleTextView.text = TodoDetails.title
        holder.detailsTextView.text = TodoDetails.details
    }

    override fun getItemCount(): Int = todoDetails.size
}