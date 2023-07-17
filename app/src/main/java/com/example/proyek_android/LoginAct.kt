package com.example.proyek_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.proyek_android.MainActivity
import com.example.proyek_android.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginAct : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions

    lateinit var _signin_google: MaterialButton

    val RC_SIGN_IN: Int = 1

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val _tv_sign_up = findViewById<TextView>(R.id.tv_signup)
        val _btn_sign_in = findViewById<Button>(R.id.btn_signin)

/*        _signin_google = findViewById(R.id.btn_google)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/

        _tv_sign_up.setOnClickListener{
            val intent = Intent(this@LoginAct, SignUpAct::class.java)
            startActivity(intent)
        }
        _btn_sign_in.setOnClickListener{
            login()
        }
    }

    private fun login(){
        val emailTxt = findViewById<TextInputEditText>(R.id.et_email)
        val passwordTxt = findViewById<TextInputEditText>(R.id.et_password)

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()

        if(!email.isEmpty() && !password.isEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if(task.isSuccessful){
                    Intent(this@LoginAct, MainActivity::class.java). also{
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        Toast.makeText(this, "Succesfully Logged in :)", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this, "Please fill up the credentials :(", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser != null){
            Intent(this@LoginAct, MainActivity::class.java). also{
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this, "Succesfully Logged in :)", Toast.LENGTH_LONG).show()
            }
        }
    }

}