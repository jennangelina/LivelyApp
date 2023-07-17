package com.example.proyek_android

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import com.example.proyek_android.MainActivity
import com.example.proyek_android.R
import com.example.proyek_android.User
import com.example.proyek_android.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignUpAct : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    lateinit var _tf_birthdate: MaterialButton
    lateinit var auth: FirebaseAuth

    lateinit var _radio_group: RadioGroup

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private lateinit var db: FirebaseFirestore
    private var arUser= arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val _tv_login = findViewById<TextView>(R.id.tv_Login)
        val _btn_signup = findViewById<Button>(R.id.btn_signup)

        val _et_name = findViewById<TextInputEditText>(R.id.et_fullname)
        val _et_email = findViewById<TextInputEditText>(R.id.et_email)
        val _et_password = findViewById<TextInputEditText>(R.id.et_password)
        val _et_confirm_password = findViewById<TextInputEditText>(R.id.et_confirmpassword)
        val _et_phoneNumber = findViewById<TextInputEditText>(R.id.et_phonenumber)

        _radio_group = findViewById(R.id.rg_gender)
        val _radio_button_male = findViewById<RadioButton>(R.id.rb_male)
        val _radio_button_female = findViewById<RadioButton>(R.id.rb_female)

        val _checkBox = findViewById<CheckBox>(R.id.cb_checkbox)

        _tf_birthdate = findViewById(R.id.tf_birthdate)

        val uid = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("User")

        _btn_signup.setOnClickListener{
            val name = _et_name.text.toString().trim()
            val email = _et_email.text.toString().trim()
            val password = _et_password.text.toString().trim()
            val confirmpassword = _et_confirm_password.text.toString().trim()
            val phoneNumber = _et_phoneNumber.text.toString().trim()
            var gender = ""

            if(name.isEmpty()){
                _et_name.error = "Please Input Your Name"
                _et_name.requestFocus()
                return@setOnClickListener
            }

            if(email.isEmpty()){
                _et_email.error = "Please Input Your Email"
                _et_email.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                _et_email.error = "Email is not valid"
                _et_email.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty() || password.length<0){
                _et_password.error = "Password must be more than 6 characters"
                _et_password.requestFocus()
                return@setOnClickListener
            }

            if(!_radio_button_male.isChecked() && !_radio_button_female.isChecked()){
                _radio_button_female.error = "Please choose your gender!"
                _radio_button_female.requestFocus()
                return@setOnClickListener
            }

            if(_radio_button_male.isChecked()){
                gender = "male"
            }
            else if(_radio_button_female.isChecked()){
                gender = "female"
            }

            if(day == 0 || month == 0 || year == 0 || savedDay == 0 || savedMonth == 0 || savedYear == 0){
                _tf_birthdate.error = "Please insert your Birth Date"
                Toast.makeText(this, "Please insert your Birth Date", Toast.LENGTH_SHORT).show()
                _tf_birthdate.requestFocus()
                return@setOnClickListener
            }

            if(confirmpassword.isEmpty() || confirmpassword != password){
                _et_confirm_password.error = "Password doens't match"
                _et_confirm_password.requestFocus()
                return@setOnClickListener
            }

            if(!Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumber.isEmpty()){
                _et_phoneNumber.error = "Phone Number is not valid"
                _et_phoneNumber.requestFocus()
                return@setOnClickListener
            }

            if(!_checkBox.isChecked()){
                _checkBox.error = "You have to agree to Signing Up"
                _checkBox.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){
                    if(it.isSuccessful){
                        var user_uid = auth.uid
                        val temp = User(name, email, phoneNumber, gender, savedDay.toString(), savedMonth.toString(), savedYear.toString())

                        db.collection("tbUser").document(user_uid.toString())
                            .set(temp)

                        Intent(this@SignUpAct, MainActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        _tv_login.setOnClickListener{
            val intent = Intent(this@SignUpAct, LoginAct::class.java)
            startActivity(intent)
        }

        _tf_birthdate.setOnClickListener{
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            Intent(this@SignUpAct, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
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