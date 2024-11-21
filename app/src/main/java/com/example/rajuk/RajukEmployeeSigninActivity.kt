package com.example.rajuk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.rajuk.Api.ApiClient
import com.example.rajuk.Api.ApiInterface
import com.example.rajuk.dataClass.EmployeeLoginRequest
import kotlinx.coroutines.launch

class RajukEmployeeSigninActivity : AppCompatActivity() {

    lateinit var employeeId: EditText
    lateinit var employeePassword: EditText
    lateinit var employeeSignin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rajuk_employee_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        employeeId = findViewById(R.id.EditTextEmployeeId)
        employeePassword = findViewById(R.id.EditTextEmployeePassword)
        employeeSignin = findViewById(R.id.buttonEmployeeLogin)

        employeeSignin.setOnClickListener {
            val enterEmployeeId = employeeId.text.toString()
            val enterEmployeePassword = employeePassword.text.toString()

            getEmployeeLogin(enterEmployeeId, enterEmployeePassword)


        }

    }


    private fun getEmployeeLogin(employeeId: String, employeePassword: String) {

        val enployeeLoginRequest = EmployeeLoginRequest(employeeId, employeePassword)
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            val response = apiService.employeeLogin(enployeeLoginRequest)
            try {
                if (response.isSuccessful) {
                    val employeeLoginResponse = response.body()
                    Toast.makeText(
                        this@RajukEmployeeSigninActivity,
                        employeeLoginResponse?.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    val sharedPreferences = getSharedPreferencess(this@RajukEmployeeSigninActivity, "employeePrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("userToken", employeeLoginResponse?.token)
                    editor.apply()

                    val intent = Intent(
                        this@RajukEmployeeSigninActivity,
                        LoginCredentialActivity::class.java
                    )
                    startActivity(intent)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@RajukEmployeeSigninActivity, errorBody, Toast.LENGTH_SHORT)
                        .show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@RajukEmployeeSigninActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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