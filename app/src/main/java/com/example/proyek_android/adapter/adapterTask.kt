package com.example.proyek_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.R
import com.example.proyek_android.task

class adapterTask (
    private val listTask: ArrayList<task>): RecyclerView.Adapter<adapterTask.ListViewHolder>() {
        inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var _taskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
            var _bulletEmpty: ImageView = itemView.findViewById(R.id.ivBulletEmpty)
            var _bulletCheck: ImageView = itemView.findViewById(R.id.ivBulletCheck)
        }

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: task, pos: Int)
        fun checkTask(data: task, pos: Int)
        fun unCheckTask(data: task, pos: Int)
    }

    fun setOnItemClickCallbcak(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemtask, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var data = listTask[position]

        holder._taskTitle.setText(data.task_title)

        if(data.task_done == 0) {
            holder._bulletCheck.visibility = View.INVISIBLE
            holder._bulletCheck.isClickable = false
        } else if(data.task_done == 1) {
            holder._bulletCheck.visibility = View.VISIBLE
            holder._bulletEmpty.visibility = View.INVISIBLE
            holder._bulletCheck.isClickable = true
        }

        holder._bulletEmpty.setOnClickListener{
            holder._bulletCheck.visibility = View.VISIBLE
            holder._bulletEmpty.visibility = View.INVISIBLE
            holder._bulletCheck.isClickable = true
            onItemClickCallback.checkTask(data, position)
        }
        holder._bulletCheck.setOnClickListener{
            holder._bulletCheck.visibility = View.INVISIBLE
            holder._bulletEmpty.visibility = View.VISIBLE
            holder._bulletCheck.isClickable = false
            onItemClickCallback.unCheckTask(data, position)
        }
        holder._taskTitle.setOnClickListener {
            onItemClickCallback.onItemClicked(data, position)
        }
    }

    override fun getItemCount(): Int {
        return listTask.count()
    }
}