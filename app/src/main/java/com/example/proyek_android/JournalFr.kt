package com.example.proyek_android

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.proyek_android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [JournalFr.newInstance] factory method to
 * create an instance of this fragment.
 */
class JournalFr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var savedDay: String
    lateinit var savedMonth: String
    lateinit var savedYear: String
    var mood = "none"

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser

    lateinit var _ivJournalHistory: ImageView
    lateinit var _tvJournalDate: TextView
    lateinit var _etJournalTitle: EditText
    lateinit var _etJournalContent: EditText
    lateinit var _saveJournalBtn: Button
    lateinit var _ivMoodHappy: ImageView
    lateinit var _ivMoodSmile: ImageView
    lateinit var _ivMoodSad: ImageView
    lateinit var _ivMoodCry: ImageView
    lateinit var _ivMoodAngry: ImageView

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
        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!

        _ivJournalHistory = view.findViewById(R.id.ivJournalHistory)
        _tvJournalDate = view.findViewById(R.id.tvJournalDate)
        _etJournalTitle = view.findViewById(R.id.etJournalTitle)
        _etJournalContent = view.findViewById(R.id.etJournalContent)
        _saveJournalBtn = view.findViewById(R.id.saveJournalBtn)
        _ivMoodHappy = view.findViewById(R.id.ivMoodHappy)
        _ivMoodSmile = view.findViewById(R.id.ivMoodSmile)
        _ivMoodSad = view.findViewById(R.id.ivMoodSad)
        _ivMoodCry = view.findViewById(R.id.ivMoodCry)
        _ivMoodAngry = view.findViewById(R.id.ivMoodAngry)

        _ivJournalHistory.setOnClickListener {
            Intent(activity, JournalHistoryAct::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)+1
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        savedDay = day.toString()
        savedMonth = month.toString()
        savedYear = year.toString()

        SearchJournal(savedDay, savedMonth, savedYear)

        _tvJournalDate.setOnClickListener {
            var datePickerDialog = DatePickerDialog(requireActivity(),
                DatePickerDialog.OnDateSetListener{view, yearD, monthD, dayD ->
                    val months = monthD+1
                    var monthStr = "0"
                    when(months.toString()) {
                        "1" -> monthStr="January"
                        "2" -> monthStr="February"
                        "3" -> monthStr="March"
                        "4" -> monthStr="April"
                        "5" -> monthStr="May"
                        "6" -> monthStr="June"
                        "7" -> monthStr="July"
                        "8" -> monthStr="August"
                        "9" -> monthStr="September"
                        "10" -> monthStr="October"
                        "11" -> monthStr="November"
                        "12" -> monthStr="December"
                    }

                    _tvJournalDate.setText("$monthStr $day, $year")
                    savedDay = dayD.toString()
                    savedMonth = months.toString()
                    savedYear = yearD.toString()
                    SearchJournal(savedDay, savedMonth, savedYear)
                }, year, month-1, day)
            datePickerDialog.show()
        }

        _ivMoodHappy.setOnClickListener {
            _ivMoodHappy.setBackgroundResource(R.drawable.mood_selected_bg)

            _ivMoodSmile.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodSad.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodCry.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodAngry.setBackgroundResource(R.drawable.mood_bg)

            mood = "happy"
        }

        _ivMoodSmile.setOnClickListener {
            _ivMoodSmile.setBackgroundResource(R.drawable.mood_selected_bg)

            _ivMoodHappy.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodSad.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodCry.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodAngry.setBackgroundResource(R.drawable.mood_bg)

            mood = "smile"
        }

        _ivMoodSad.setOnClickListener {
            _ivMoodSad.setBackgroundResource(R.drawable.mood_selected_bg)

            _ivMoodSmile.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodHappy.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodCry.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodAngry.setBackgroundResource(R.drawable.mood_bg)

            mood = "sad"
        }

        _ivMoodCry.setOnClickListener {
            _ivMoodCry.setBackgroundResource(R.drawable.mood_selected_bg)

            _ivMoodSmile.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodSad.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodHappy.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodAngry.setBackgroundResource(R.drawable.mood_bg)

            mood = "cry"
        }

        _ivMoodAngry.setOnClickListener {
            _ivMoodAngry.setBackgroundResource(R.drawable.mood_selected_bg)

            _ivMoodSmile.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodSad.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodCry.setBackgroundResource(R.drawable.mood_bg)
            _ivMoodHappy.setBackgroundResource(R.drawable.mood_bg)

            mood = "angry"
        }

        _saveJournalBtn.setOnClickListener {
            if(_etJournalTitle.text.isEmpty()) {
                Toast.makeText(activity, "Please insert a title", Toast.LENGTH_SHORT).show()
                _etJournalTitle.requestFocus()
                return@setOnClickListener
            }
            if(_etJournalContent.text.isEmpty()) {
                _etJournalContent.requestFocus()
                Toast.makeText(activity, "Please describe your day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(mood == "none") {
                Toast.makeText(activity, "Please select your mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // save to firebase
            val docId = ("$savedDay$savedMonth$savedYear")
            val data = journal(_etJournalTitle.text.toString(), _etJournalContent.text.toString(), mood, savedDay, savedMonth, savedYear)
            db.collection("journals").document(fb_user!!.uid).collection("myJournals").document(docId)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(activity, "Your entry has been recorded", Toast.LENGTH_SHORT).show()
                    Log.d("Firebase", "Journal is successfully added")
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }
        }
    }

    private fun SearchJournal(_day: String, _month: String, _year: String) {
        var monthStr = "0"
        when(_month) {
            "1" -> monthStr="January"
            "2" -> monthStr="February"
            "3" -> monthStr="March"
            "4" -> monthStr="April"
            "5" -> monthStr="May"
            "6" -> monthStr="June"
            "7" -> monthStr="July"
            "8" -> monthStr="August"
            "9" -> monthStr="September"
            "10" -> monthStr="October"
            "11" -> monthStr="November"
            "12" -> monthStr="December"
        }

        _tvJournalDate.setText("$monthStr $_day, $_year")

        val docId = ("$savedDay$savedMonth$savedYear")
        val journalRef = db.collection("journals").document(fb_user!!.uid).collection("myJournals").document(docId)
        journalRef.get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    val dataJournal = journal(document.data!!.get("journal_title").toString(),
                        document.data!!.get("journal_content").toString(),
                        document.data!!.get("journal_mood").toString(),
                        document.data!!.get("journal_day").toString(),
                        document.data!!.get("journal_month").toString(),
                        document.data!!.get("journal_year").toString())

                    _etJournalTitle.setText(dataJournal.journal_title)
                    _etJournalContent.setText(dataJournal.journal_content)
                    mood = dataJournal.journal_mood
                    when(mood) {
                        "happy" -> _ivMoodHappy.setBackgroundResource(R.drawable.mood_selected_bg)
                        "smile" -> _ivMoodSmile.setBackgroundResource(R.drawable.mood_selected_bg)
                        "sad" -> _ivMoodSad.setBackgroundResource(R.drawable.mood_selected_bg)
                        "cry" -> _ivMoodCry.setBackgroundResource(R.drawable.mood_selected_bg)
                        "angry" -> _ivMoodAngry.setBackgroundResource(R.drawable.mood_selected_bg)
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Firebase", "Failed to read journal data")
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment JournalFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            JournalFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}