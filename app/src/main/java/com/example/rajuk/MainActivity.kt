package com.example.rajuk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    lateinit var logIn : Button
    lateinit var signUp: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        logIn = findViewById(R.id.buttonLogin)
        signUp = findViewById(R.id.textViewzSignUp)

        signUp.setOnClickListener {
            val intent = Intent(this@MainActivity,UserSignupActivity::class.java)
            startActivity(intent)
        }

        logIn.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginCredentialActivity::class.java)
            startActivity(intent)
        }


    }
}