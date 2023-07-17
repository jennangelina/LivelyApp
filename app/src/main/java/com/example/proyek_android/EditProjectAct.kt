package com.example.proyek_android

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterTask
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditProjectAct : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    lateinit var _etProjectDeadline3: MaterialButton
    private lateinit var _rvTaskInEditProject: RecyclerView
    private var arTaskInEditProject = arrayListOf<task>()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!

        val dataProject = intent.getParcelableExtra<project>("projectData")

        _rvTaskInEditProject = findViewById(R.id.rvTaskInNewProject)
        val _etProjectTitle3 = findViewById<EditText>(R.id.etProjectTitle3)
        _etProjectDeadline3 = findViewById(R.id.etProjectDeadline3)
        val _ivAddNewTaskInEditProject = findViewById<ImageView>(R.id.ivAddNewTaskInEditProject)
        val _tvAddNewTaskInEditProject = findViewById<TextView>(R.id.tvAddNewTaskInEditProject)
        val _saveEditProjectBtn = findViewById<Button>(R.id.saveEditProjectBtn)
        val _deleteEditProjectBtn = findViewById<Button>(R.id.deleteEditProjectBtn3)
        val _cancelEditProjectBtn = findViewById<Button>(R.id.cancelEditProjectBtn)
        val _dropdownStatusItem3 = findViewById<AutoCompleteTextView>(R.id.dropdownStatusItem3)

        _dropdownStatusItem3.setText(dataProject?.project_status)
        val projectstatus = resources.getStringArray(R.array.projectstatus)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, projectstatus)
        _dropdownStatusItem3.setAdapter(arrayAdapter)

        _etProjectTitle3.setText(dataProject?.project_title)

        _etProjectDeadline3.setOnClickListener {
            day = dataProject?.deadline_day.toString().toInt()
            month = dataProject?.deadline_month.toString().toInt()
            year = dataProject?.deadline_year.toString().toInt()

            DatePickerDialog(this, this, year, month, day).show()
        }

        DisplayTaskData(dataProject!!)

        _ivAddNewTaskInEditProject.setOnClickListener {
            showBottomSheet(dataProject!!)
        }
        _tvAddNewTaskInEditProject.setOnClickListener {
            showBottomSheet(dataProject!!)
        }
        _saveEditProjectBtn.setOnClickListener {
            if(_etProjectTitle3.text.isEmpty()){
                _etProjectTitle3.error = "Please fill the project title"
                _etProjectTitle3.requestFocus()
                return@setOnClickListener
            }

            val editedProject = project(dataProject!!.project_id,
                _etProjectTitle3.text.toString(),
                _dropdownStatusItem3.text.toString(),
                savedDay.toString(),
                savedMonth.toString(), savedYear.toString(), 0)
            db.collection("projects").document(fb_user!!.uid).collection("myProjects").document(dataProject.project_id)
                .set(editedProject)
                .addOnSuccessListener {
                    Toast.makeText(this, "Project updated", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update project", Toast.LENGTH_LONG).show()
                }
            val intent = Intent(this@EditProjectAct, MainActivity::class.java)
            startActivity(intent)
        }
        _deleteEditProjectBtn.setOnClickListener {
            db.collection("projects").document(fb_user!!.uid).collection("myProjects").document(dataProject!!.project_id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Project deleted", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete project", Toast.LENGTH_LONG).show()
                }
            val intent = Intent(this@EditProjectAct, MainActivity::class.java)
            startActivity(intent)
        }
        _cancelEditProjectBtn.setOnClickListener {
            val intent = Intent(this@EditProjectAct, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showBottomSheet(dataProject: project) {
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_newtask)

        val _etNewTaskTitle = dialog.findViewById<EditText>(R.id.etEditTaskTitle)
        val _addNewTaskBtn = dialog.findViewById<Button>(R.id.saveEditTaskBtn)
        val _cancelAddNewTaskBtn = dialog.findViewById<Button>(R.id.deleteTaskBtn)

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        _addNewTaskBtn.setOnClickListener {
            if (_etNewTaskTitle.text.toString().isEmpty()) {
                Toast.makeText(this, "Task title is required", Toast.LENGTH_LONG).show()
            } else {
                val docRef =
                    db.collection("projects").document(fb_user!!.uid).collection("myProjects")
                        .document(dataProject.project_id).collection("tasklist").document()
                val data = task(docRef.id, _etNewTaskTitle.text.toString(), 0)
                docRef.set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task added", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add task", Toast.LENGTH_LONG).show()
                    }

                _etNewTaskTitle.setText("")
                DisplayTaskData(dataProject)

                dialog.dismiss()
            }

            _cancelAddNewTaskBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun DisplayTaskData(dataProject: project) {
        _rvTaskInEditProject.layoutManager = LinearLayoutManager(this)
        val adapterT = adapterTask(arTaskInEditProject)
        _rvTaskInEditProject.adapter = adapterT

        adapterT.setOnItemClickCallbcak(object: adapterTask.OnItemClickCallback {
            // edit task
            override fun onItemClicked(data: task, pos: Int) {
                var tasktitle = data.task_title
                var taskid = data.task_id
                showBottomSheetEdit(tasktitle, taskid, dataProject)
            }

            override fun checkTask(data: task, pos: Int) {
                var newdata = task(data.task_id, data.task_title, 1)
                db.collection("projects").document(fb_user!!.uid).collection("myProjects")
                    .document(dataProject.project_id).collection("tasklist").document(data.task_id)
                    .set(newdata)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Task is checked")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
            }

            override fun unCheckTask(data: task, pos: Int) {
                var newdata = task(data.task_id, data.task_title, 0)
                db.collection("projects").document(fb_user!!.uid).collection("myProjects")
                    .document(dataProject.project_id).collection("tasklist").document(data.task_id)
                    .set(newdata)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Task is unchecked")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
            }
        })

        // read from firestore
        db.collection("projects").document(fb_user.uid).collection("myProjects").document(dataProject.project_id).collection("tasklist")
            .get()
            .addOnSuccessListener { result ->
                arTaskInEditProject.clear()
                for(document in result){
                    val dataTask = task(document.id, document.data.get("task_title").toString(), document.data.get("task_done").toString().toInt())
                    arTaskInEditProject.add(dataTask)
                }
                adapterT.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    // show dialog bottom sheet to edit / delete data
    private fun showBottomSheetEdit(title: String, id:String, dataProject: project) {
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_edittask)

        val _etEditTaskTitle = dialog.findViewById<EditText>(R.id.etEditTaskTitle)
        val _saveEditTaskBtn = dialog.findViewById<Button>(R.id.saveEditTaskBtn)
        val _deleteTaskBtn = dialog.findViewById<Button>(R.id.deleteTaskBtn)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        _etEditTaskTitle.setText(title)

        _saveEditTaskBtn.setOnClickListener {
            if(!_etEditTaskTitle.text.toString().isEmpty() && _etEditTaskTitle.text.toString() != title){
//                arTask.add(data)
//                _rvTask.adapter?.notifyItemInserted(arTask.size-1)

                // edit ke firebase
                val data = task(id, _etEditTaskTitle.text.toString(), 0) // 0 = false, 1 = true
                db.collection("projects").document(fb_user.uid).collection("myProjects").document(dataProject.project_id).collection("tasklist").document(id)
                    .set(data)
                    .addOnSuccessListener {
                        DisplayTaskData(dataProject)
                    }
                    .addOnFailureListener {
                    }

                _etEditTaskTitle.setText("")
                dialog.dismiss()
            }
        }

        _deleteTaskBtn.setOnClickListener {
            db.collection("projects").document(fb_user.uid).collection("myProjects").document(dataProject.project_id).collection("tasklist").document(id)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Task from " + dataProject.project_title + " is successfully deleted from the firebase")
                    DisplayTaskData(dataProject)
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }
            dialog.dismiss()
        }
    }

    private fun getDateTimeCalendar(){
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        _etProjectDeadline3.setText("$savedDay-$savedMonth-$savedYear")
    }
}