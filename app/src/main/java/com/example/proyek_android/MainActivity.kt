package com.example.proyek_android

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterProject
import com.example.proyek_android.adapter.adapterTask
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var _rvTask: RecyclerView
    private var arTask = arrayListOf<task>()

    private val _rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val _rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val _fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val _toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }
    private var clicked = false

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    lateinit var _tvCountIncompleteTask: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!

        _rvTask = findViewById(R.id.rvTask)

        val _addBtn = findViewById<FloatingActionButton>(R.id.addBtn)
        val _addTaskBtn = findViewById<FloatingActionButton>(R.id.addTaskBtn)
        val _addProjectBtn = findViewById<FloatingActionButton>(R.id.addProjectBtn)
        val _bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        val _navController = findNavController(R.id.fragmentContainer)
        _tvCountIncompleteTask = findViewById<TextView>(R.id.tvCountIncompleteTask)

        _bottomNavView.setBackground(null)
        _bottomNavView.setupWithNavController(_navController)

        _addBtn.setOnClickListener {
            if(!clicked) {
                // visibility
                _addTaskBtn.visibility = View.VISIBLE
                _addProjectBtn.visibility = View.VISIBLE
                // animation
                _addTaskBtn.startAnimation(_fromBottom)
                _addProjectBtn.startAnimation(_fromBottom)
                _addBtn.startAnimation(_rotateOpen)
                // isclickeed
                _addTaskBtn.isClickable = true
                _addProjectBtn.isClickable = true
            } else {
                // visibility
                _addTaskBtn.visibility = View.INVISIBLE
                _addProjectBtn.visibility = View.INVISIBLE
                // animation
                _addTaskBtn.startAnimation(_toBottom)
                _addProjectBtn.startAnimation(_toBottom)
                _addBtn.startAnimation(_rotateClose)
                // isclickeed
                _addTaskBtn.isClickable = false
                _addProjectBtn.isClickable = false

            }
            clicked = !clicked
        }

        _addTaskBtn.setOnClickListener {
            showBottomSheet()
        }
        _addProjectBtn.setOnClickListener {
            Intent(this@MainActivity, AddNewProjectAct::class.java). also{
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    // bottom sheet untuk add new task
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
                // add to firebase
                val docRef = db.collection("tasks").document(fb_user!!.uid).collection("myTasks").document()
                val docId = docRef.id
                val data = task(docId, _etNewTaskTitle.text.toString(), 0) // 0 = false, 1 = true
                docRef.set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Task added", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add task", Toast.LENGTH_LONG).show()
                    }
                DisplayTaskData()
                _etNewTaskTitle.setText("")
                dialog.dismiss()
            }
        }

        _cancelAddNewTaskBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun DisplayTaskData() {
        _rvTask.layoutManager = LinearLayoutManager(this)
        val adapterT = adapterTask(arTask)
        _rvTask.adapter = adapterT

        adapterT.setOnItemClickCallbcak(object: adapterTask.OnItemClickCallback {
            // edit task
            override fun onItemClicked(data: task, pos: Int) {
                var tasktitle = data.task_title
                var taskid = data.task_id
                showBottomSheetEdit(tasktitle, taskid)
            }

            override fun checkTask(data: task, pos: Int) {
                var newdata = task(data.task_id, data.task_title, 1)
                db.collection("tasks").document(fb_user!!.uid).collection("myTasks").document(data.task_id)
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
                db.collection("tasks").document(fb_user!!.uid).collection("myTasks").document(data.task_id)
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
        db.collection("tasks").document(fb_user.uid).collection("myTasks")
            .whereEqualTo("task_done", 0)
            .get()
            .addOnSuccessListener { result ->
                arTask.clear()
                for(document in result){
                    val dataTask = task(document.id, document.data.get("task_title").toString(), document.data.get("task_done").toString().toInt())
                    arTask.add(dataTask)
                }
                adapterT.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun showBottomSheetEdit(title: String, id:String) {
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
                // edit ke firebase
                val data = task(id, _etEditTaskTitle.text.toString(), 0) // 0 = false, 1 = true
                db.collection("tasks").document(fb_user!!.uid).collection("myTasks").document(id)
                    .set(data)
                    .addOnSuccessListener {
                        DisplayTaskData()
                    }
                    .addOnFailureListener {
                    }

                _etEditTaskTitle.setText("")
                dialog.dismiss()
            }
        }

        _deleteTaskBtn.setOnClickListener {
            ShowConfirmDialogTask(id)
            dialog.dismiss()
            countIncompleteTask()
        }
    }

    private fun ShowConfirmDialogTask(docId: String) {
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_confirmation_dialog)

        val _tvAreYouSure = dialog.findViewById<TextView>(R.id.tvAreYouSure)
        val _deleteConfirmBtn = dialog.findViewById<Button>(R.id.deleteConfirmBtn)
        val _cancelConfirmBtn = dialog.findViewById<Button>(R.id.cancelConfirmBtn)

        _tvAreYouSure.setText("Are you sure you want to delete this task?")

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.CENTER)

        _deleteConfirmBtn.setOnClickListener {
            db.collection("tasks").document(fb_user!!.uid).collection("myTasks").document(docId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Task is successfully deleted from the firebase")
                    DisplayTaskData()
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }
            dialog.dismiss()
        }

        _cancelConfirmBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun countIncompleteTask() {
        var co = 0
        db.collection("tasks").document(fb_user!!.uid).collection("myTasks")
            .whereEqualTo("task_done", 0)
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    co = co+1
                    Log.d("FirebaseIncompleteTask", co.toString())
                }
                _tvCountIncompleteTask.setText(co.toString())
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }
}