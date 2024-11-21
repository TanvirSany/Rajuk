package com.example.rajuk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginCredentialActivity : AppCompatActivity() {

    lateinit var addComplain : CardView
    lateinit var previousComplain : CardView

    lateinit var logout : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_credential)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addComplain = findViewById(R.id.addComplain)
        previousComplain = findViewById(R.id.previousComplain)
        logout = findViewById(R.id.button)

        addComplain.setOnClickListener{
            val intent = Intent(this@LoginCredentialActivity, ComplainActivity::class.java)
            startActivity(intent)
        }

        previousComplain.setOnClickListener{
            val intent = Intent(this@LoginCredentialActivity, PreviousComplainActivity::class.java)
            startActivity(intent)

        }

        logout.setOnClickListener{
            clearSharedPreferences(this@LoginCredentialActivity)
            val intent = Intent(this@LoginCredentialActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }



    private fun clearSharedPreferences(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}