package com.example.rajuk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import kotlinx.coroutines.launch
import retrofit2.Response
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

        image.setOnClickListener{
            if(checkCameraPermission()){
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureLauncher.launch(takePictureIntent)
            }else{
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
        val registerRequest =
            RegisterRequest(userName, userPhoneNumber, userNidNumber, userPassword, userImage)

        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            try {
                val response: Response<RegisterResponse> = apiService.register(registerRequest)
                if (response.isSuccessful) {
                    val successResponse = response.body()
                    if (successResponse != null) {
                        Toast.makeText(this@UserSignupActivity, "Success", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        this@UserSignupActivity,
                        response.errorBody().toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } catch (e: Exception) {
                Toast.makeText(this@UserSignupActivity, "exception: " + e.message, Toast.LENGTH_SHORT).show()
                Log.e("here", "submit: $e.message.toString()", )
            }

        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.getParcelable<Bitmap>("data") as? Bitmap
            imageBitmap?.let {
                image.setImageBitmap(it)
                val base64String = bitmapToBase64(it)
                imageUrl = base64String
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode,
//            data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data")
//                    as Bitmap
//            val base64String = bitmapToBase64(imageBitmap)
//            imageUrl = base64String
//        }
//    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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



