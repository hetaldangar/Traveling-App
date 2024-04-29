package com.example.travelingproject.Activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.travelingproject.MainActivity
import com.example.travelingproject.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import android.os.Handler
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding : ActivitySplashScreenBinding
    lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
    }

    private fun initView() {
        auth = Firebase.auth

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            Handler().postDelayed({

                val i = Intent(this@SplashScreenActivity, DashboardActivity::class.java)
                startActivity(i)
                finish()

            }, 1000)
        } else {
            Handler().postDelayed({

                val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(i)
                finish()

            }, 1000)
            }
        }
}