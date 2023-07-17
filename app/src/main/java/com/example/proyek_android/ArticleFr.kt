package com.example.proyek_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Activity
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.res.TypedArray
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.webkit.WebViewDatabase.getInstance
import android.widget.*
import android.widget.TextView
import android.widget.Toast
import android.content.Context.SENSOR_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.content.Context.NOTIFICATION_SERVICE
import com.example.proyek_android.adapter.adapterArticle

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFr.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // shake
//    private var mSensorManager: SensorManager? = null
//    private var mAccelerometer: Sensor? = null
//    private var mShakeDetector: ShakeDetector? = null
//    private var tvShake: TextView? = null

    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore
    lateinit var _ll_article : LinearLayout
    lateinit var _btn_book : Button
    lateinit var _btn_productiv : Button
    lateinit var _btn_study : Button
    lateinit var _btn_motivation : Button
    private lateinit var _rvArticles: RecyclerView
    private var arArticles =arrayListOf<Articles>()
    lateinit var binding : ActivityMainBinding

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
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        _rvArticles = view.findViewById(R.id.recycler_view_article)

        _btn_book = view.findViewById(R.id.btn_book)
        _btn_productiv = view.findViewById(R.id.btn_productivity)
        _btn_study = view.findViewById(R.id.btn_study)
        _btn_motivation = view.findViewById(R.id.btn_motivation)

        DisplayArticles()

        _btn_book.setOnClickListener{
            Intent(activity, books_recommend::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        _btn_productiv.setOnClickListener {
            Intent(activity, productivity_tips::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        _btn_study.setOnClickListener {
            Intent(activity, study_tips::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        _btn_motivation.setOnClickListener {
            Intent(activity, motivation_tips::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    private fun DisplayArticles() {
        _rvArticles.layoutManager = LinearLayoutManager(activity)
        val adapterArt= adapterArticle(arArticles)
        _rvArticles.adapter = adapterArt

        adapterArt.setOnItemClickCallback(object: adapterArticle.OnItemClickCallBack{
            override fun onItemClicked(data: Articles) {
                Intent(activity, article_detail::class.java).also {
                    it.putExtra("articles", data)
                    startActivity(it)
                }
            }
        })

        // read from firestore
        db.collection("articles").get()
            .addOnSuccessListener { show ->
                arArticles.clear()
                for(document in show){
                    val dataArticle= Articles(document.data.get("article_title").toString(), document.data.get("article_photo").toString(),document.data.get("article_date").toString(),document.data.get("article_category").toString(),document.data.get("article_content").toString())

                    arArticles.add(dataArticle)
                }
                adapterArt.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("Firebase", it.message.toString())
            }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ArticleFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ArticleFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}