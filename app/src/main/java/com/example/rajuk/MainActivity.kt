package com.example.rajuk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.rajuk.Api.ApiClient
import com.example.rajuk.Api.ApiInterface
import com.example.rajuk.dataClass.LoginRequest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var logIn : Button
    lateinit var signUp: TextView
    lateinit var phoneNumber : EditText
    lateinit var password : EditText
    lateinit var rajukEmployeeSignin : TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var anonymousSignin : Button


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
        phoneNumber = findViewById(R.id.EditTextLoginPhoneNumber)
        password = findViewById(R.id.EditTextLoginPassword)
        rajukEmployeeSignin = findViewById(R.id.textViewRajukEmployee)
        anonymousSignin = findViewById(R.id.buttonAnonymousSignin)


        signUp.setOnClickListener {
            val intent = Intent(this@MainActivity,UserSignupActivity::class.java)
            startActivity(intent)
        }

        logIn.setOnClickListener {
            val enterPhoneNumber = phoneNumber.text.toString()
            val enterPassword = password.text.toString()


            getLogIn(enterPhoneNumber, enterPassword)
        }


        rajukEmployeeSignin.setOnClickListener {
            val intent = Intent(this@MainActivity, RajukEmployeeSigninActivity::class.java)
            startActivity(intent)
        }

        anonymousSignin.setOnClickListener {
            val intent = Intent(this@MainActivity, ComplainActivity::class.java)
            startActivity(intent)
        }


    }


    fun getLogIn(Phone : String, Password : String) {
        val loginRequest = LoginRequest(Phone, Password)

        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            val response = apiService.login(loginRequest)
            try {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Toast.makeText(this@MainActivity, loginResponse?.message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MainActivity, LoginCredentialActivity::class.java)
                    startActivity(intent)

                    sharedPreferences = getSharedPreferencess(this@MainActivity,"userPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("userToken", loginResponse?.token)
                    editor.putString("name", loginResponse?.user?.name)
                    editor.apply()



                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MainActivity, errorBody, Toast.LENGTH_SHORT).show()

                }
            }catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }



    private fun getSharedPreferencess(context: Context, fileName: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
        return context.getSharedPreferences(fileName, mode)
    }

    //for disappear keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0) }
        return super.dispatchTouchEvent(ev)
    }
}