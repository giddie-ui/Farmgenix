package com.example.farmgenix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class splash : AppCompatActivity() {
    private val SPLASH_TIME = 4000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (FirebaseAuth.getInstance().currentUser == null)
        {

            startActivity(Intent(this, signup::class.java))

        }
        else {
            Handler().postDelayed({

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, SPLASH_TIME)
        }
    }
}