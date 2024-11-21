package com.example.rajuk

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rajuk.Api.ApiClient
import com.example.rajuk.Api.ApiInterface
import com.example.rajuk.dataClass.ComplainListAdapter
import kotlinx.coroutines.launch

class PreviousComplainActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var recyclerView : RecyclerView

    lateinit var plotType : TextView
    lateinit var address : TextView
    lateinit var details : TextView
    lateinit var status : TextView

    lateinit var plotTypeText : String
    lateinit var addressText : String
    lateinit var detailsText : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_previous_complain)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        recyclerView = findViewById(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        showComplain()



    }


    fun showComplain() {
        val apiService = ApiClient.retrofit.create(ApiInterface::class.java)

        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        var token = sharedPreferences.getString("userToken", "")
        token = "Bearer $token"

        lifecycleScope.launch {
            val response = apiService.complainList(accept = "application/json", token)

            try {
                if (response.isSuccessful) {
                    val complainResponse = response.body()
                    val complainList = complainResponse?.complains

                    plotTypeText = complainList?.map { it.plotType }.toString()
                    addressText = complainList?.map { it.houseNo+", "+it.road+", "+it.cityCorporationName+", "+it.thanaName }.toString()
                    detailsText = complainList?.map { it.details }.toString()

                    if (complainList != null) {
                        val adapter = ComplainListAdapter(complainList)
                        recyclerView.adapter = adapter
                    }

                } else{
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@PreviousComplainActivity, "Error: $errorBody", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@PreviousComplainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }


}