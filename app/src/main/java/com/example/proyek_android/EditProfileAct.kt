package com.example.proyek_android

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditProfileAct : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var db : FirebaseFirestore

    lateinit var _iv_profile : ImageView

    lateinit var _et_nama : EditText
    lateinit var _et_email : EditText
    lateinit var _et_phoneNumber : EditText

    lateinit var _rg_gender : RadioGroup
    lateinit var _rb_male : RadioButton
    lateinit var _rb_female : RadioButton

    lateinit var _tf_birthdate : MaterialButton

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser

        val _btn_logout = findViewById<Button>(R.id.btn_logout)
        val _btn_savechanges = findViewById<Button>(R.id.btn_savechanges)

        _iv_profile = findViewById(R.id.iv_edit_profile)

        _et_nama = findViewById(R.id.et_edit_name)
        _et_email = findViewById(R.id.et_edit_email)
        _et_phoneNumber = findViewById(R.id.et_edit_phonenumber)

        _rg_gender = findViewById(R.id.rg_edit_gender)
        _rb_male = findViewById(R.id.rb_edit_male)
        _rb_female = findViewById(R.id.rb_edit_female)
        var gender = ""

        _tf_birthdate = findViewById(R.id.tf_edit_birthdate)

        val userUID = auth.uid
        RefreshData()

        _tf_birthdate.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }

        _btn_savechanges.setOnClickListener {
            val name = _et_nama.text.toString()
            val email = _et_email.text.toString().trim()
            val phoneNumber = _et_phoneNumber.text.toString().trim()
            if (name.isEmpty()) {
                _et_nama.error = "Please Input Your Name"
                _et_nama.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                _et_email.error = "Please Input Your Email"
                _et_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _et_email.error = "Email is not valid"
                _et_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumber.isEmpty()) {
                _et_phoneNumber.error = "Phone Number is not valid"
                _et_phoneNumber.requestFocus()
                return@setOnClickListener
            }

            if (!_rb_male.isChecked && !_rb_female.isChecked) {
                _rb_female.error = "Please choose your gender!"
                _rb_female.requestFocus()
                return@setOnClickListener
            }

            if (_rb_male.isChecked()) {
                gender = "male"
            } else if (_rb_female.isChecked()) {
                gender = "female"
            }

            if ((day == 0 || month == 0 || year == 0) && (savedDay == 0 || savedMonth == 0 || savedYear == 0)) {
                _tf_birthdate.error = "Please insert your Birth Date"
                Toast.makeText(this, "Please insert your Birth Date", Toast.LENGTH_SHORT).show()
                _tf_birthdate.requestFocus()
                return@setOnClickListener
            }

            user?.updateEmail(email)
            val temp = User(
                name,
                email,
                phoneNumber,
                gender,
                savedDay.toString(),
                savedMonth.toString(),
                savedYear.toString()
            )
            db.collection("tbUser").document(userUID.toString())
                .set(temp)
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_LONG).show()
        }

        _btn_logout.setOnClickListener {
            auth.signOut()
            Intent(this@EditProfileAct, LoginAct::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this@EditProfileAct, "Logged Out", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun RefreshData(){
        val userUID = auth.uid
        db.collection("tbUser").document(userUID.toString()).get()
            .addOnSuccessListener {
                _et_nama.setText(it.data?.get("nama").toString())
                _et_email.setText(it.data?.get("email").toString())
                _et_phoneNumber.setText(it.data?.get("phoneNumber").toString())
                if(it.data?.get("gender").toString() == "male"){
                    _rg_gender.check(R.id.rb_edit_male)
                    _iv_profile.setImageResource(R.drawable.profile_picture_male)
                }
                else if(it.data?.get("gender").toString() == "female"){
                    _rg_gender.check(R.id.rb_edit_female)
                    _iv_profile.setImageResource(R.drawable.profile_picture_female)
                }
                val day_temp = it.data?.get("day_user").toString()
                val month_temp = it.data?.get("month_user").toString()
                val year_temp = it.data?.get("year_user").toString()
                savedDay = day_temp.toInt()
                savedMonth = month_temp.toInt()
                savedYear = year_temp.toInt()
                _tf_birthdate.setText("$day_temp-$month_temp-$year_temp")
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
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        _tf_birthdate.setText("$savedDay-$savedMonth-$savedYear")
    }
}