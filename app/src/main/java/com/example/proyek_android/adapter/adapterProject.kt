package com.example.proyek_android.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.R
import com.example.proyek_android.project
import com.example.proyek_android.task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class adapterProject (
    private val listProject: ArrayList<project>): RecyclerView.Adapter<adapterProject.ListViewHolder>() {
        inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var _projectTitle: TextView = itemView.findViewById(R.id.tvProjectTitle)
            var _projectStatus: TextView = itemView.findViewById(R.id.tvProjectStatus3)
            var _projectDeadline: TextView = itemView.findViewById(R.id.tvProjectDeadline)
            var _ivArrowDownProject: ImageView = itemView.findViewById(R.id.ivArrowDownProject)
            var _tvLoadMore: TextView = itemView.findViewById(R.id.tvLoadMore)
            var _finishProjectBtn: Button = itemView.findViewById(R.id.finishProjectBtn)
            var _projectContainer: ConstraintLayout = itemView.findViewById(R.id.projectContainer)
            var _rvTaskforProject: RecyclerView = itemView.findViewById(R.id.rvTaskforProject)

            // baru
            var listTaskforProject: ArrayList<task> = arrayListOf<task>()
            lateinit var auth: FirebaseAuth
            lateinit var db: FirebaseFirestore
            lateinit var fb_user: FirebaseUser
            var adapterT = adapterTaskforProject(listTaskforProject)
//            var act = MainActivity
        }

    private lateinit var context: Context

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: project)
        fun finishProjectClicked(data: project)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemproject, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }
 
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var data = listProject[position]

        holder._projectTitle.setText(data.project_title)

        holder._projectStatus.setText(data.project_status)

        holder._projectContainer.setOnClickListener {
            onItemClickCallback.onItemClicked(data)
        }

        var monthStr = "0"
        when(data.deadline_month) {
            "1" -> monthStr="Jan"
            "2" -> monthStr="Feb"
            "3" -> monthStr="Mar"
            "4" -> monthStr="Apr"
            "5" -> monthStr="May"
            "6" -> monthStr="Jun"
            "7" -> monthStr="Jul"
            "8" -> monthStr="Aug"
            "9" -> monthStr="Sep"
            "10" -> monthStr="Oct"
            "11" -> monthStr="Nov"
            "12" -> monthStr="Dec"
        }

        holder._projectDeadline.setText(data.deadline_day+" "+monthStr+" "+data.deadline_year)

        holder._finishProjectBtn.setOnClickListener {
            onItemClickCallback.finishProjectClicked(data)
        }

        holder.auth = FirebaseAuth.getInstance()
        holder.db = FirebaseFirestore.getInstance()
        holder.fb_user = holder.auth.currentUser!!

        holder._rvTaskforProject.layoutManager = LinearLayoutManager(context)
        holder.adapterT = adapterTaskforProject(holder.listTaskforProject)
        holder._rvTaskforProject.adapter = holder.adapterT

        holder.adapterT.setOnItemClickCallbcak(object: adapterTaskforProject.OnItemClickCallback {
            override fun checkTask(dataTask: task, pos: Int) {
                var newdata = task(dataTask.task_id, dataTask.task_title, 1)
                holder.db.collection("projects").document(holder.fb_user!!.uid).collection("myProjects")
                    .document(data.project_id).collection("tasklist").document(dataTask.task_id)
                    .set(newdata)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Task is checked")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
            }

            override fun unCheckTask(dataTask: task, pos: Int) {
                var newdata = task(dataTask.task_id, dataTask.task_title, 0)
                holder.db.collection("projects").document(holder.fb_user!!.uid).collection("myProjects")
                    .document(data.project_id).collection("tasklist").document(dataTask.task_id)
                    .set(newdata)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Task is checked")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
            }

        })

        var count = 0
        holder.db.collection("projects").document(holder.fb_user.uid).collection("myProjects").document(data.project_id).collection("tasklist")
            .whereEqualTo("task_done", 0)
            .get()
            .addOnSuccessListener { result ->
                holder.listTaskforProject.clear()
                for(document in result){
                    if(count <= 3) {
                        val dataTask = task(document.id, document.data.get("task_title").toString(), document.data.get("task_done").toString().toInt())
                        holder.listTaskforProject.add(dataTask)
                    }
                    count=count+1
                }
                holder.adapterT.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    override fun getItemCount(): Int {
        return listProject.count()
    }
}
