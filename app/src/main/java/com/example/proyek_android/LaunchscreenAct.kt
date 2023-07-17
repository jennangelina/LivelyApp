package com.example.proyek_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.proyek_android.MainActivity
import com.example.proyek_android.R
import com.google.firebase.auth.FirebaseAuth

class LaunchscreenAct : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launchscreen)

        auth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            if(auth.currentUser == null){
                Intent(this@LaunchscreenAct, LoginAct::class.java). also{
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            } else {
                Intent(this@LaunchscreenAct, MainActivity::class.java). also{
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            }
        }, 3000)
    }
}