package com.example.rajuk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.rajuk.Api.ApiClient
import com.example.rajuk.Api.ApiInterface
import com.example.rajuk.dataClass.RegisterRequest
import com.example.rajuk.dataClass.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class UserSignupActivity : AppCompatActivity() {

    lateinit var image: ImageView
    lateinit var name: EditText
    lateinit var phoneNumber: EditText
    lateinit var nidNumber: EditText
    lateinit var password: EditText
    lateinit var register: Button
    lateinit var imageUrl : String

    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_CODE = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        name = findViewById(R.id.EditTextName)
        phoneNumber = findViewById(R.id.EditTextPhoneNumber)
        nidNumber = findViewById(R.id.EditTextNidNumber)
        password = findViewById(R.id.EditTextPassword)
        register = findViewById(R.id.buttonRegister)
        image = findViewById(R.id.imageViewRegisterImage)





        register.setOnClickListener {
            val userName = name.text.toString()
            val userPhoneNumber = phoneNumber.text.toString()
            val userNidNumber = nidNumber.text.toString()
            val userPassword = password.text.toString()
            val imageUri = imageUrl


            submit(userName, userPhoneNumber, userNidNumber, userPassword, imageUri)
        }

        image.setOnClickListener {
            if (checkCameraPermission()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(takePictureIntent)
            } else {
                requestCameraPermission()
            }
        }
    }


    fun submit(
        userName: String,
        userPhoneNumber: String,
        userNidNumber: String,
        userPassword: String,
        userImage: String
    ) {
        Log.e("here", "submit: " + userImage )

        val registerRequest = RegisterRequest(userName, userPhoneNumber, userNidNumber, userPassword, userImage)
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            try {
                val response = apiService.register(registerRequest)
                if (response.isSuccessful) {
                    val successResponse = response.body()
                    Toast.makeText(this@UserSignupActivity, successResponse?.message ?: "Success", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("here", errorBody.toString())

                    if (response.code() == 422 && errorBody != null) {
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, RegisterResponse::class.java)

                        errorResponse.errors?.name?.let { name.error = it.joinToString(", ") }
                        errorResponse.errors?.phone?.let { phoneNumber.error = it.joinToString(", ") }
                        errorResponse.errors?.nid?.let { nidNumber.error = it.joinToString(", ") }
                        errorResponse.errors?.password?.let { password.error = it.joinToString(", ") }
                    } else {
                        Toast.makeText(this@UserSignupActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e( "Error during registration", e.message.toString())
                Toast.makeText(this@UserSignupActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                image.setImageBitmap(it)
                val (base64String, format) = bitmapToBase64(it, Bitmap.CompressFormat.PNG, 80) // Get base64 and format
                val mimeType = when (format) {
                    Bitmap.CompressFormat.PNG -> "image/png"
                    Bitmap.CompressFormat.JPEG -> "image/jpeg"
                    else -> "image/*" // Default to generic image type
                }
                imageUrl = "data:$mimeType;base64,$base64String" // Construct Data URL
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): Pair<String, Bitmap.CompressFormat> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Pair(Base64.encodeToString(byteArray, Base64.DEFAULT), format) // Return base64 and format
    }


    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode
            == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with capturing selfie
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(takePictureIntent)

            } else {
                // Handle permission denied case
                // (e.g., show a toast message informing the user)
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}



