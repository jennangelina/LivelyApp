package com.example.proyek_android

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class DetailJournalAct : AppCompatActivity() {
    lateinit var savedDay: String
    lateinit var savedMonth: String
    lateinit var savedYear: String

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var fb_user: FirebaseUser
    lateinit var _tvJournalDateDet:TextView
    lateinit var _etJournalTitleDet: EditText
    lateinit var _etJournalContentDet:EditText
    lateinit var _saveEditJournalBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_journal)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fb_user = auth.currentUser!!


        _tvJournalDateDet = findViewById(R.id.tvJournalDateDet)
        _etJournalTitleDet = findViewById(R.id.etJournalTitleDet)
        _etJournalContentDet = findViewById(R.id.etJournalContentDet)
        _saveEditJournalBtn = findViewById(R.id.saveEditJournalBtn)
        val _ivMoodHappyDet = findViewById<ImageView>(R.id.ivMoodHappyDet)
        val _ivMoodSmileDet = findViewById<ImageView>(R.id.ivMoodSmileDet)
        val _ivMoodSadDet = findViewById<ImageView>(R.id.ivMoodSadDet)
        val _ivMoodCryDet = findViewById<ImageView>(R.id.ivMoodCryDet)
        val _ivMoodAngryDet = findViewById<ImageView>(R.id.ivMoodAngryDet)
        val _deleteEditJournalBtn = findViewById<Button>(R.id.deleteEditJournalBtn)

        // get data passed from journal history
        val _journalData = intent.getParcelableExtra<journal>("journalData")
        var mood = _journalData!!.journal_mood

        // set journal date, title, content, & mood
        setJournalDate(_journalData!!.journal_day, _journalData!!.journal_month, _journalData!!.journal_year)
        _etJournalTitleDet.setText(_journalData!!.journal_title)
        _etJournalContentDet.setText(_journalData!!.journal_content)
        when(_journalData.journal_mood) {
            "happy" -> _ivMoodHappyDet.setBackgroundResource(R.drawable.rectangle_outline2)
            "smile" -> _ivMoodSmileDet.setBackgroundResource(R.drawable.rectangle_outline2)
            "sad" -> _ivMoodSadDet.setBackgroundResource(R.drawable.rectangle_outline2)
            "cry" -> _ivMoodCryDet.setBackgroundResource(R.drawable.rectangle_outline2)
            "angry" -> _ivMoodAngryDet.setBackgroundResource(R.drawable.rectangle_outline2)
        }

        // mood tapped
        _ivMoodHappyDet.setOnClickListener {
            _ivMoodHappyDet.setBackgroundResource(R.drawable.rectangle_outline2)

            _ivMoodSmileDet.setBackgroundResource(0)
            _ivMoodSadDet.setBackgroundResource(0)
            _ivMoodCryDet.setBackgroundResource(0)
            _ivMoodAngryDet.setBackgroundResource(0)
            mood = "happy"
        }

        _ivMoodSmileDet.setOnClickListener {
            _ivMoodSmileDet.setBackgroundResource(R.drawable.rectangle_outline2)

            _ivMoodHappyDet.setBackgroundResource(0)
            _ivMoodSadDet.setBackgroundResource(0)
            _ivMoodCryDet.setBackgroundResource(0)
            _ivMoodAngryDet.setBackgroundResource(0)
            mood = "smile"
        }

        _ivMoodSadDet.setOnClickListener {
            _ivMoodSadDet.setBackgroundResource(R.drawable.rectangle_outline2)

            _ivMoodSmileDet.setBackgroundResource(0)
            _ivMoodHappyDet.setBackgroundResource(0)
            _ivMoodCryDet.setBackgroundResource(0)
            _ivMoodAngryDet.setBackgroundResource(0)
            mood = "sad"
        }

        _ivMoodCryDet.setOnClickListener {
            _ivMoodCryDet.setBackgroundResource(R.drawable.rectangle_outline2)

            _ivMoodSmileDet.setBackgroundResource(0)
            _ivMoodSadDet.setBackgroundResource(0)
            _ivMoodHappyDet.setBackgroundResource(0)
            _ivMoodAngryDet.setBackgroundResource(0)
            mood = "cry"
        }

        _ivMoodAngryDet.setOnClickListener {
            _ivMoodAngryDet.setBackgroundResource(R.drawable.rectangle_outline2)

            _ivMoodSmileDet.setBackgroundResource(0)
            _ivMoodSadDet.setBackgroundResource(0)
            _ivMoodCryDet.setBackgroundResource(0)
            _ivMoodHappyDet.setBackgroundResource(0)
            mood = "angry"
        }

        // save edit changes
        _saveEditJournalBtn.setOnClickListener {
            if(_etJournalTitleDet.text.isEmpty()) {
                Toast.makeText(this, "Please fill in the title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(_etJournalContentDet.text.isEmpty()) {
                Toast.makeText(this, "Please fill in the content", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(_etJournalTitleDet.text.toString() != _journalData.journal_title || _etJournalContentDet.text.toString() != _journalData.journal_content) {
                val docId = "$savedDay$savedMonth$savedYear"
                val newdata = journal(_etJournalTitleDet.text.toString(), _etJournalContentDet.text.toString(), mood, savedDay, savedMonth, savedYear)
                db.collection("journals").document(fb_user!!.uid).collection("myJournals").document(docId)
                    .set(newdata)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Your entry has been recorded", Toast.LENGTH_SHORT).show()
                        Log.d("Firebase", "Journal is successfully added")
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to record your entry", Toast.LENGTH_SHORT).show()
                        Log.d("Firebase", it.message.toString())
                    }
            }
        }

        // delete
        _deleteEditJournalBtn.setOnClickListener {
            ShowConfirmDialog(_journalData)
        }
    }

    private fun ShowConfirmDialog(data: journal) {
        var dialog: Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.alert_confirmation_dialog)

        val _deleteConfirmBtn = findViewById<Button>(R.id.deleteConfirmBtn)
        val _cancelConfirmBtn = findViewById<Button>(R.id.cancelConfirmBtn)

        dialog.show()
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes!!.windowAnimations = R.style.BottomSheetAnimation
        dialog.window!!.setGravity(Gravity.CENTER)

        _deleteConfirmBtn.setOnClickListener {
            db.collection("journals").document(fb_user!!.uid).collection("myJournals").document("${data.journal_day}${data.journal_month}${data.journal_year}")
                .delete()
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
            dialog.dismiss()
        }

        _cancelConfirmBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setJournalDate(_day: String, _month: String, _year: String) {
        var monthStr = ""
        when (_month) {
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
        _tvJournalDateDet.setText("$monthStr $_day, $_year")
    }
}