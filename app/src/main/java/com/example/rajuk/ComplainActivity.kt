package com.example.rajuk

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
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
import com.example.rajuk.dataClass.ComplainRequest
import com.example.rajuk.dataClass.ComplainResponse
import com.example.rajuk.dataClass.ImageData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ComplainActivity : AppCompatActivity() {

    lateinit var plotType: AutoCompleteTextView
    lateinit var chooseCityCorporation: AutoCompleteTextView
    lateinit var chooseThana: AutoCompleteTextView
    lateinit var wordNo: EditText
    lateinit var houseNo: EditText
    lateinit var roadNo: EditText
    lateinit var approval: EditText
    lateinit var imageLeft: ImageView
    lateinit var imageRight: ImageView
    lateinit var imageTop: ImageView
    lateinit var imageBottom: ImageView
    lateinit var longitude: EditText
    lateinit var latitude: EditText
    lateinit var location: Button
    lateinit var details: EditText
    lateinit var complainSubmit: Button

    var currentImageData: ImageData? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var sharedPreferences: SharedPreferences
    var selectedThanaId: Int? = null
    var selectedCityCorporationId: Int? = null



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
        wordNo = findViewById(R.id.EditTextWordNo)
        houseNo = findViewById(R.id.EditTextHouseNo)
        roadNo = findViewById(R.id.EditTextRoadNo)
        approval = findViewById(R.id.EditTextApproval)
        longitude = findViewById(R.id.EditTextLongitude)
        latitude = findViewById(R.id.EditTextLatitude)
        location = findViewById(R.id.buttonLocation)
        details = findViewById(R.id.EditTextDetails)
        complainSubmit = findViewById(R.id.buttonComplainSubmit)


        //Adapter for capture dropdown
        val plotTypes = resources.getStringArray(R.array.plot_type)
        val adapter = ArrayAdapter(this, R.layout.item_list, plotTypes)
        plotType.setAdapter(adapter)

        //get dropdown list
        getThana()
        getCityCorporation()

        //image data
        val imageDataList = listOf(
            ImageData(imageLeft),
            ImageData(imageRight),
            ImageData(imageTop),
            ImageData(imageBottom)
        )

        for (imageData in imageDataList) {
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

        //Capture location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        location.setOnClickListener {
            getLocation()
        }

        //Submit complain
        complainSubmit.setOnClickListener {
            val inputPlotType = plotType.text.toString()
            val inputHouseNo = houseNo.text.toString()
            val inputRoad = roadNo.text.toString()
            val inputCityCorporationId = selectedCityCorporationId
            val inputThanaId = selectedThanaId
            val inputLat = latitude.text.toString()
            val inputLon = longitude.text.toString()
            val inputDetails = details.text.toString()
            val inputFrontImage = imageDataList[0].imageUrl
            val inputBackImage = imageDataList[1].imageUrl
            val inputRightImage = imageDataList[2].imageUrl
            val inputLeftImage = imageDataList[3].imageUrl
            val inputApproval = approval.text.toString()

            Log.e("rajuk", "onCreate:$inputPlotType ")
            Log.e("rajuk", "onCreate:$inputHouseNo ")
            Log.e("rajuk", "onCreate:$inputRoad ")
            Log.e("rajuk", "onCreate:$inputCityCorporationId ")
            Log.e("rajuk", "onCreate:$inputThanaId ")
            Log.e("rajuk", "onCreate:$inputLat ")
            Log.e("rajuk", "onCreate:$inputLon ")
            Log.e("rajuk", "onCreate:$inputDetails ")
            Log.e("rajuk", "onCreate:$inputFrontImage ")
            Log.e("rajuk", "onCreate:$inputBackImage ")
            Log.e("rajuk", "onCreate:$inputRightImage ")
            Log.e("rajuk", "onCreate:$inputLeftImage ")
            Log.e("rajuk", "onCreate:$inputApproval ")


        if (validateForm()){

            if(currentImageData == null){
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

            var token : String? = sharedPreferences.getString("userToken", "")


            if(token != null){
                submit(
                    inputPlotType,
                    inputHouseNo,
                    inputRoad,
                    inputCityCorporationId!!,
                    inputThanaId!!,
                    inputLat,
                    inputLon,
                    inputDetails,
                    inputFrontImage!!,
                    inputBackImage!!,
                    inputRightImage!!,
                    inputLeftImage!!,
                    inputApproval
                )
            }else{
                submitWithoutToken(
                    inputPlotType,
                    inputHouseNo,
                    inputRoad,
                    inputCityCorporationId!!,
                    inputThanaId!!,
                    inputLat,
                    inputLon,
                    inputDetails,
                    inputFrontImage!!,
                    inputBackImage!!,
                    inputRightImage!!,
                    inputLeftImage!!,
                    inputApproval
                )
            }

        }




        }


    }//onCreate


    fun validateForm(): Boolean {
        var isValid = true

        if (plotType.text.toString().isEmpty()) {
            plotType.error = "This field is required"
            isValid = false
        }
        if(chooseCityCorporation.text.toString().isEmpty()){
            chooseCityCorporation.error = "This field is required"
            isValid = false
        }
        if(chooseThana.text.toString().isEmpty()){
            chooseThana.error = "This field is required"
        }
        if(wordNo.text.toString().isEmpty()){
            wordNo.error = "This field is required"
        }
        if(houseNo.text.toString().isEmpty()){
            houseNo.error = "This field is required"
        }
        if(roadNo.text.toString().isEmpty()) {
            roadNo.error = "This field is required"
        }
        if(approval.text.toString().isEmpty()){
            approval.error = "This field is required"
        }
        if (details.text.toString().isEmpty()){
            details.error = "This field is required"
        }

        if(isValid){
            plotType.error = null
        }

        return isValid
    }


    //getCityCorporation list for dropdown
    fun getCityCorporation(){
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        lifecycleScope.launch {
            val response = apiService.cityCorporation()

            if(response.isSuccessful){
                val cityCorporationResponse = response.body()
                val cityCorporation = cityCorporationResponse?.cityCorporations?.map { it.name }?: emptyList()

                val adapter = ArrayAdapter(this@ComplainActivity, R.layout.item_list, cityCorporation)
                chooseCityCorporation.setAdapter(adapter)

                chooseCityCorporation.setOnItemClickListener { parent, view, position, id ->
                    val selectedCityCorporation = cityCorporation[position]
                    selectedCityCorporationId = cityCorporationResponse?.cityCorporations?.get(position)?.id
                }
            }
        }
    }



//    Get thana list for dropdown
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

                chooseThana.setOnItemClickListener{
                        parent, view, position, id ->
                    val selectedThana = thana[position]
                     selectedThanaId = thana_id[position]
                }
            }
        }


    }


    //Location function
    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )

            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                longitude.setText(location.longitude.toString())
                latitude.setText(location.latitude.toString())
            }
        }

    }

    //Submit with Token
    fun submit(
        inputPlotType: String,
        inputHouseNo: String,
        inputRoad: String,
        inputCityCorporationId: Int,
        inputThanaId: Int,
        inputLat: String,
        inputLon: String,
        inputDetails: String,
        inputFrontImage: String,
        inputBackImage: String,
        inputRightImage: String,
        inputLeftImage: String,
        inputApproval: String
    ){
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)
        val complainRequest = ComplainRequest(inputPlotType, inputHouseNo, inputRoad, inputCityCorporationId, inputThanaId, inputLat, inputLon, inputDetails, inputFrontImage, inputBackImage, inputRightImage, inputLeftImage,inputApproval)

        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        var token = sharedPreferences.getString("userToken", "")
        token = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = apiService.complain(token,contentType = "application/json",accept = "application/json",complainRequest)
                if(response.isSuccessful){
                    val complainResponse = response.body()

                    val intent = Intent(this@ComplainActivity, LoginCredentialActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this@ComplainActivity, complainResponse?.message, Toast.LENGTH_SHORT).show()

                }else{
                    val errorBody = response.errorBody()?.string()

                   if(response.code() == 422 && errorBody != null){

                       val gson = Gson()
                       val errorResponse = gson.fromJson(errorBody, ComplainResponse::class.java)

                       errorResponse.errors?.plotType?.let { plotType.error = it.joinToString(", ") }
                       errorResponse.errors?.houseNo?.let { houseNo.error = it.joinToString(", ") }
                       errorResponse.errors?.road?.let { roadNo.error = it.joinToString(", ") }
                       errorResponse.errors?.cityCorporationId?.let { chooseCityCorporation.error = it.joinToString(", ") }
                       errorResponse.errors?.thanaId?.let { chooseThana.error = it.joinToString(", ") }
                       errorResponse.errors?.details?.let { details.error = it.joinToString(", ") }

                   }else{
                       Toast.makeText(this@ComplainActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                   }

                }

            } catch (e: Exception) {
                Toast.makeText(this@ComplainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }


        }
    }

    //Submit without token
    fun submitWithoutToken(
        inputPlotType: String,
        inputHouseNo: String,
        inputRoad: String,
        inputCityCorporationId: Int,
        inputThanaId: Int,
        inputLat: String,
        inputLon: String,
        inputDetails: String,
        inputFrontImage: String,
        inputBackImage: String,
        inputRightImage: String,
        inputLeftImage: String,
        inputApproval: String
    ){
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)
        val complainRequest = ComplainRequest(inputPlotType, inputHouseNo, inputRoad, inputCityCorporationId, inputThanaId, inputLat, inputLon, inputDetails, inputFrontImage, inputBackImage, inputRightImage, inputLeftImage,inputApproval)


        lifecycleScope.launch {
            try {
                val response = apiService.guestComplain(contentType = "application/json",accept = "application/json",complainRequest)
                if(response.isSuccessful){
                    val complainResponse = response.body()

                    val intent = Intent(this@ComplainActivity, MainActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this@ComplainActivity, complainResponse?.message, Toast.LENGTH_SHORT).show()

                }else{
                    val errorBody = response.errorBody()?.string()

                    if(response.code() == 422 && errorBody != null){

                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, ComplainResponse::class.java)

                        errorResponse.errors?.plotType?.let { plotType.error = it.joinToString(", ") }
                        errorResponse.errors?.houseNo?.let { houseNo.error = it.joinToString(", ") }
                        errorResponse.errors?.road?.let { roadNo.error = it.joinToString(", ") }
                        errorResponse.errors?.cityCorporationId?.let { chooseCityCorporation.error = it.joinToString(", ") }
                        errorResponse.errors?.thanaId?.let { chooseThana.error = it.joinToString(", ") }
                        errorResponse.errors?.details?.let { details.error = it.joinToString(", ") }

                    }else{
                        Toast.makeText(this@ComplainActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                    }

                }

            } catch (e: Exception) {
                Toast.makeText(this@ComplainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    //for disappear keyboard
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0) }
        return super.dispatchTouchEvent(ev)
    }


}






//Accessing image data: You can access the Base64-encoded image URLs for each
//ImageView using the imageUrl property of the corresponding ImageData object
//in the imageDataList. For example:
//
//val imageUrl1 = imageDataList[0].imageUrl
//val imageUrl2 = imageDataList[1].imageUrl
