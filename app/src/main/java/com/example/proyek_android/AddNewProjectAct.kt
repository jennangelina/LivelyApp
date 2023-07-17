package com.example.proyek_android

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class AddNewProjectAct : AppCompatActivity(), DatePickerDialog.OnDateSetListener{
    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    lateinit var _etProjectDeadline: MaterialButton
    lateinit var _rvTaskInNewProject: RecyclerView
    private var arTask = arrayListOf<task>()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_project)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!

        val _etProjectTitle = findViewById<EditText>(R.id.etProjectTitle)
        _etProjectDeadline = findViewById(R.id.etProjectDeadline)
        _rvTaskInNewProject = findViewById(R.id.rvTaskInNewProject)
        val _ivAddNewTaskInNewProject = findViewById<ImageView>(R.id.ivAddNewTaskInNewProject)
        val _tvAddNewTaskInNewProject = findViewById<TextView>(R.id.tvAddNewTaskInEditProject)
        val _createNewProjectBtn = findViewById<Button>(R.id.saveEditProjectBtn)
        val _cancelNewProjectBtn = findViewById<Button>(R.id.cancelNewProjectBtn)

        // dropdown for select project status
        val _dropdownStatusItem = findViewById<AutoCompleteTextView>(R.id.dropdownStatusItem)
        val projectstatus = resources.getStringArray(R.array.projectstatus)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, projectstatus)
        _dropdownStatusItem.setAdapter(arrayAdapter)

        _etProjectDeadline.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }

        _ivAddNewTaskInNewProject.setOnClickListener {
            showBottomSheet()
        }

        _tvAddNewTaskInNewProject.setOnClickListener {
            showBottomSheet()
        }

        _createNewProjectBtn.setOnClickListener {
            // check all the required fields
            val title = _etProjectTitle.text.toString()
            val status = _dropdownStatusItem.text.toString()

            if(title.isEmpty()){
                _etProjectTitle.error = "Please fill the project title"
                _etProjectTitle.requestFocus()
                return@setOnClickListener
            }

            if(status.isEmpty()){
                _dropdownStatusItem.error = "Please select a status"
                _dropdownStatusItem.requestFocus()
                return@setOnClickListener
            }

            if(day == 0 || month == 0 || year == 0 || savedDay == 0 || savedMonth == 0 || savedYear == 0){
                _etProjectDeadline.error = "Please insert a deadline"
//                Toast.makeText(this, "Please insert a deadline", Toast.LENGTH_SHORT).show()
                _etProjectDeadline.requestFocus()
                return@setOnClickListener
            }
            //firebase
            val projectRef = db.collection("projects").document(fb_user!!.uid).collection("myProjects").document()
            val data = project(projectRef.id, title, status, savedDay.toString(), savedMonth.toString(), savedYear.toString(), 0)
            projectRef.set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Project created", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create project", Toast.LENGTH_LONG).show()
                }


            for(taskitem in arTask) {
                projectRef.collection("tasklist").document().set(taskitem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task added to database", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add task to database", Toast.LENGTH_LONG).show()
                    }
            }
            val intent = Intent(this@AddNewProjectAct, MainActivity::class.java)
            startActivity(intent)
        }

        _cancelNewProjectBtn.setOnClickListener {
            val intent = Intent(this@AddNewProjectAct, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getDateTimeCalendar(){
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month+1
        savedYear = year

        getDateTimeCalendar()
        _etProjectDeadline.setText("$savedDay-$savedMonth-$savedYear")
    }

    private fun showBottomSheet() {
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_newtask)

        val _etNewTaskTitle = dialog.findViewById<EditText>(R.id.etEditTaskTitle)
        val _addNewTaskBtn = dialog.findViewById<Button>(R.id.saveEditTaskBtn)
        val _cancelAddNewTaskBtn = dialog.findViewById<Button>(R.id.deleteTaskBtn)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        _addNewTaskBtn.setOnClickListener {
            if(_etNewTaskTitle.text.toString().isEmpty()){
                Toast.makeText(this, "Task title is required", Toast.LENGTH_LONG).show()
            } else {
                val data = task(arTask.count().toString(), _etNewTaskTitle.text.toString(), 0)
                arTask.add(data)
                _rvTaskInNewProject.adapter?.notifyItemInserted(arTask.size-1)

//                //firebase
//                db.collection("tasks").document(fb_user!!.uid).collection("myTasks").document().set(data)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Task added", Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(this, "Failed to add task", Toast.LENGTH_LONG).show()
//                    }

                _etNewTaskTitle.setText("")
                DisplayTaskData()

                dialog.dismiss()
            }
        }

        _cancelAddNewTaskBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showSuccessDialog(){
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_success_dialog)

        var _tvSuccess = findViewById<TextView>(R.id.tvSuccess)
        _tvSuccess.setText("Project has been created")

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.CENTER)

    }

    private fun DisplayTaskData() {
        _rvTaskInNewProject.layoutManager = LinearLayoutManager(this)
        val adapterT = adapterTask(arTask)
        _rvTaskInNewProject.adapter = adapterT
    }
}