package com.example.proyek_android

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.adapter.adapterProject
import com.example.proyek_android.adapter.adapterTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFr.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _rvTask: RecyclerView
    private lateinit var _rvProject: RecyclerView
    private var arTask = arrayListOf<task>()
    private var arProject = arrayListOf<project>()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    lateinit var _tvCountIncompleteTask: TextView
    lateinit var _tvCountIncompleteProject: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!

        _rvTask = view.findViewById(R.id.rvTask)
        _rvProject = view.findViewById(R.id.rvProject)

        var _tvDate = view.findViewById<TextView>(R.id.tvDate)
        var _tvHiName = view.findViewById<TextView>(R.id.tvHiName)

        DisplayTaskData()
        DisplayProjectData()

        // get today's date
        val sdf = SimpleDateFormat("EEE, d MMM yyyy")
        val currentDate = sdf.format(Date())
        _tvDate.setText(currentDate)

        // get user's name
        val userUID = auth.uid
        db.collection("tbUser").document(userUID.toString()).get()
            .addOnSuccessListener {
                _tvHiName.setText("Hi, "+it.data?.get("nama").toString())
            }

        // get count for incomplete task
        _tvCountIncompleteTask = view.findViewById<TextView>(R.id.tvCountIncompleteTask)
        _tvCountIncompleteProject = view.findViewById<TextView>(R.id.tvCountIncompleteProject)
        countIncompleteTask()
        countIncompleteProject()

    }

    private fun DisplayTaskData() {
        _rvTask.layoutManager = LinearLayoutManager(activity)
        val adapterT = adapterTask(arTask)
        _rvTask.adapter = adapterT

        adapterT.setOnItemClickCallbcak(object: adapterTask.OnItemClickCallback {
            // edit task
            override fun onItemClicked(data: task, pos: Int) {
                var tasktitle = data.task_title
                var taskid = data.task_id
                showBottomSheet(tasktitle, taskid)
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
            .whereEqualTo("task_done", 0) // hanya menampilkan task yg blm selesai
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

    private fun DisplayProjectData() {
        _rvProject.layoutManager = LinearLayoutManager(activity)
        val adapterP = adapterProject(arProject)
        _rvProject.adapter = adapterP

        adapterP.setOnItemClickCallback(object: adapterProject.OnItemClickCallback {
            override fun onItemClicked(data: project) {
                showBottomSheetProject(data)
            }

            override fun finishProjectClicked(data: project) {
                val editedProject = project(data.project_id,
                    data.project_title,
                    data.project_status,
                    data.deadline_day,
                    data.deadline_month,
                    data.deadline_year,
                    1)
                db.collection("projects").document(fb_user!!.uid).collection("myProjects").document(data.project_id)
                    .set(editedProject)
                    .addOnSuccessListener {
                        DisplayProjectData()
                        Toast.makeText(activity, "Project finished", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Failed to finish project", Toast.LENGTH_LONG).show()
                    }
            }
        })

        // read from firestore
        db.collection("projects").document(fb_user.uid).collection("myProjects")
            .whereEqualTo("project_ended", 0) // hanya menampilkan project yg blm selesai
            .get()
            .addOnSuccessListener { result ->
                arProject.clear()
                for(document in result){
                    val dataProject = project(document.id,
                        document.data.get("project_title").toString(),
                        document.data.get("project_status").toString(),
                        document.data.get("deadline_day").toString(),
                        document.data.get("deadline_month").toString(),
                        document.data.get("deadline_year").toString(),
                        document.data.get("project_ended").toString().toInt())
                    arProject.add(dataProject)
                }
                adapterP.notifyDataSetChanged()
                countIncompleteProject()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    private fun showBottomSheetProject(data: project) {
        var dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_sheet_project)

        var arraytask = arrayListOf<task>()
        val _tvProjectTitle2 = dialog.findViewById<TextView>(R.id.tvProjectTitle2)
        val _tvProjectStatus2 = dialog.findViewById<TextView>(R.id.tvProjectStatus2)
        val _tvProjectDeadline2 = dialog.findViewById<TextView>(R.id.tvProjectDeadline2)
        val _rvTaskforProject2 = dialog.findViewById<RecyclerView>(R.id.rvTaskforProject2)
        val _llDeleteProject2 = dialog.findViewById<LinearLayout>(R.id.llDeleteProject2)
        val _llEditProject2 = dialog.findViewById<LinearLayout>(R.id.llEditProject2)
        val _llFinishProject2 = dialog.findViewById<LinearLayout>(R.id.llFinishProject2)

        _tvProjectTitle2.text = data.project_title
        _tvProjectStatus2.text = data.project_status
        _tvProjectDeadline2.text = ("Deadline: "+(data.deadline_day)+"-"+(data.deadline_month)+"-"+(data.deadline_year))

        _rvTaskforProject2.layoutManager = LinearLayoutManager(activity)
        val adapterT = adapterTask(arraytask)
        _rvTaskforProject2.adapter = adapterT

        db.collection("projects").document(fb_user.uid).collection("myProjects").document(data.project_id).collection("tasklist")
            .get()
            .addOnSuccessListener { result ->
                arraytask.clear()
                for(document in result) {
                    val taskdata = task(document.id, document.data.get("task_title").toString(), document.data.get("task_done").toString().toInt())
                    arraytask.add(taskdata)
                }
                adapterT.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }

        adapterT.setOnItemClickCallbcak(object : adapterTask.OnItemClickCallback{
            override fun onItemClicked(data: task, pos: Int) {
                TODO("Not yet implemented")
            }

            override fun checkTask(dataT: task, pos: Int) {
                var newdata = task(dataT.task_id, dataT.task_title, 1)
                db.collection("projects").document(fb_user.uid).collection("myProjects").document(data.project_id).collection("tasklist").document(dataT.task_id)
                    .set(newdata)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Task is checked")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
            }

            override fun unCheckTask(dataT: task, pos: Int) {
                var newdata = task(dataT.task_id, dataT.task_title, 0)
                db.collection("projects").document(fb_user.uid).collection("myProjects").document(data.project_id).collection("tasklist").document(dataT.task_id)
                    .set(newdata)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Task is checked")
                    }
                    .addOnFailureListener {
                        Log.d("Firebase", it.message.toString())
                    }
            }
        })

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)

        _llDeleteProject2.setOnClickListener {
            ShowConfirmDialogProject(data.project_id)
            dialog.dismiss()
        }

        _llEditProject2.setOnClickListener {
            Intent(activity, EditProjectAct::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                it.putExtra("projectData", data)
                startActivity(it)
            }
            dialog.dismiss()
        }

        _llFinishProject2.setOnClickListener {
            val newData = project(data.project_id, data.project_title, data.project_status, data.deadline_day, data.deadline_month, data.deadline_year, 1)
            db.collection("projects").document(fb_user!!.uid).collection("myProjects").document(data.project_id)
                .set(newData)
                .addOnSuccessListener {
                    DisplayProjectData()
                }
                .addOnFailureListener {
                }
            dialog.dismiss()
        }
    }

    // show dialog bottom sheet to edit / delete data
    private fun showBottomSheet(title: String, id:String) {
        var dialog: Dialog = Dialog(requireContext())
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
        var dialog: Dialog = Dialog(requireContext())
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

    private fun ShowConfirmDialogProject(docId: String) {
        var dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_confirmation_dialog)

        val _tvAreYouSure = dialog.findViewById<TextView>(R.id.tvAreYouSure)
        val _deleteConfirmBtn = dialog.findViewById<Button>(R.id.deleteConfirmBtn)
        val _cancelConfirmBtn = dialog.findViewById<Button>(R.id.cancelConfirmBtn)

        _tvAreYouSure.setText("Are you sure you want to delete this project?")

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.CENTER)

        _deleteConfirmBtn.setOnClickListener {
            db.collection("projects").document(fb_user!!.uid).collection("myProjects").document(docId)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Project is successfully deleted from the firebase")
                    DisplayProjectData()
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

    private fun countIncompleteProject() {
        var co = 0
        db.collection("projects").document(fb_user!!.uid).collection("myProjects")
            .whereEqualTo("project_ended", 0)
            .get()
            .addOnSuccessListener { result ->
                for(document in result) {
                    co = co+1
                    Log.d("FirebaseIncompleteProject", co.toString())
                }
                _tvCountIncompleteProject.setText(co.toString())
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}