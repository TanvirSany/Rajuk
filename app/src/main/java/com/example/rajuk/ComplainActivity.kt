package com.example.rajuk

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.rajuk.Api.ApiClient
import com.example.rajuk.Api.ApiInterface
import com.example.rajuk.dataClass.ImageData
import kotlinx.coroutines.launch
import okhttp3.Response
import java.io.ByteArrayOutputStream

class ComplainActivity : AppCompatActivity() {

    lateinit var plotType : AutoCompleteTextView
    lateinit var chooseCityCorporation : AutoCompleteTextView
    lateinit var chooseThana : AutoCompleteTextView
    lateinit var wordNo : EditText
    lateinit var houseNo : EditText
    lateinit var roadNo : EditText
    lateinit var approval : EditText
    lateinit var imageLeft : ImageView
    lateinit var imageRight: ImageView
    lateinit var imageTop: ImageView
    lateinit var imageBottom: ImageView

    var currentImageData : ImageData? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_complain)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        plotType = findViewById(R.id.autoCompleteTextViewPlotType)
        chooseCityCorporation = findViewById(R.id.autoCompleteTextViewCityCorporation)
        chooseThana = findViewById(R.id.autoCompleteTextViewThana)
        imageRight = findViewById(R.id.imageViewRight)
        imageTop = findViewById(R.id.imageViewUp)
        imageBottom = findViewById(R.id.imageViewDown)
        imageLeft = findViewById(R.id.imageViewLeft)
        

        val plotTypes = resources.getStringArray(R.array.plot_type)
        val adapter = ArrayAdapter(this, R.layout.item_list, plotTypes)
        plotType.setAdapter(adapter)

        //get list
        getThana()
        getCityCorporation()
        
        val imageDataList = listOf(
            ImageData(imageLeft),
            ImageData(imageRight),
            ImageData(imageTop),
            ImageData(imageBottom)
        )


        for(imageData in imageDataList){
            imageData.imageView.setOnClickListener {
                currentImageData = imageData

                val options = arrayOf("Take Photo", "Choose from Gallery")
                AlertDialog.Builder(this)
                    .setTitle("Select Image Source")
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> checkCameraPermissionAndTakePicture()
                            1 -> checkStoragePermissionAndChooseImage()
                        }
                    }
                    .show()
            }
        }

        

    }//onCreate




    fun getCityCorporation(){
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            val response = apiService.cityCorporation()

            if(response.isSuccessful){
                val cityCorporationResponse = response.body()
                val cityCorporation = cityCorporationResponse?.cityCorporations?.map { it.name }?: emptyList()

                val adapter = ArrayAdapter(this@ComplainActivity, R.layout.item_list, cityCorporation)
                chooseCityCorporation.setAdapter(adapter)
            }
        }
    }



    //Get thana list for dropdown
    fun getThana(){
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            val response = apiService.thana()

            if (response.isSuccessful){
                val thanaResponse = response.body()
                val thana = thanaResponse?.thanas?.map{it.name}?: emptyList()
                val thana_id = thanaResponse?.thanas?.map{it.id}?: emptyList()

                val adapter = ArrayAdapter(this@ComplainActivity,R.layout.item_list,thana)
                chooseThana.setAdapter(adapter)
            }
        }
    }









    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let { handleImageUri(it) }
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { handleImageBitmap(it) }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all {
                it.value == true
            }
            if (granted) {
                // All permissions granted, proceed with the action
                if (permissions[android.Manifest.permission.CAMERA] == true) {
                    takePicture() // Open the camera
                } else if (permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true ||
                    permissions[android.Manifest.permission.READ_MEDIA_IMAGES] == true) {
                    chooseImageFromGallery() // Open the gallery
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }


    private fun handleImageUri(imageUri: Uri) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        handleImageBitmap(imageBitmap)
    }

    private fun handleImageBitmap(imageBitmap: Bitmap) {
        currentImageData?.let {
            it.imageView.setImageBitmap(imageBitmap)
            val (base64String, format) = bitmapToBase64(imageBitmap, Bitmap.CompressFormat.PNG, 80)
            val mimeType = when (format) {
                Bitmap.CompressFormat.PNG -> "image/png"
                Bitmap.CompressFormat.JPEG -> "image/jpeg"
                else -> "image/*"
            }
            it.imageUrl = "data:$mimeType;base64,$base64String"
        }
    }

    private fun checkCameraPermissionAndTakePicture() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture()
        } else {
            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.CAMERA))
        }
    }

//    private fun checkStoragePermissionAndChooseImage() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            chooseImageFromGallery()
//        } else {
//            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE))
//        }
//    }

    private fun checkStoragePermissionAndChooseImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE

        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
        {
            chooseImageFromGallery()
        } else {
            requestPermissionLauncher.launch(arrayOf(permission))
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun chooseImageFromGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(pickImageIntent)
    }

    private fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): Pair<String, Bitmap.CompressFormat> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Pair(Base64.encodeToString(byteArray, Base64.DEFAULT), format) // Return base64 and format
    }
}


//Accessing image data: You can access the Base64-encoded image URLs for each
//ImageView using the imageUrl property of the corresponding ImageData object
//in the imageDataList. For example:
//
//val imageUrl1 = imageDataList[0].imageUrl
//val imageUrl2 = imageDataList[1].imageUrl
