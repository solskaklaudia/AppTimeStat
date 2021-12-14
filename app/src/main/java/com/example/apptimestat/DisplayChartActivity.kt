package com.example.apptimestat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import retrofit2.Retrofit


class DisplayChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chart)

        val timePeriod = intent.extras?.get(EXTRA_TIME)
        val textView = findViewById<TextView>(R.id.textView2)
        textView.setText(getString(R.string.chart_title) + timePeriod.toString())

        val pieChartView = findViewById<PieChartView>(R.id.chart)
        val pieData: MutableList<SliceValue> = ArrayList()

        pieData += setSliceValue(15f, R.color.moonstoneBlue, "instagram")
        pieData += setSliceValue(25f, R.color.ashGray, "messenger")
        pieData += setSliceValue(10f, R.color.purpleLight, "tiktok")
        pieData += setSliceValue(60f, R.color.spanishPink, "snapchat")
        pieData += setSliceValue(70f, R.color.pearl, "youtube")

        val pieChartData = PieChartData(pieData)

        // labels
        pieChartData.setHasLabels(true)
        pieChartData.valueLabelTextSize = 14

        pieChartData.setHasCenterCircle(true)
        pieChartData.centerText1 = "App usage time"
        pieChartData.centerText1FontSize = 20
        pieChartData.centerText1Color = Color.parseColor("#0097A7")

        pieChartView.pieChartData = pieChartData

        getMethod()

    }

    private fun setSliceValue(value: Float, color: Int, label: String): SliceValue {
        val context: Context = applicationContext
        return SliceValue(value, ContextCompat.getColor(context, color)).setLabel(label)
    }

    fun getMethod() {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apptimestat.eu.pythonanywhere.com")
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {

            val userId = "xd"
            val dateRange = 7

            // Do the GET request and get response
            val response = service.getData(userId, dateRange)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )

                    Log.d("Pretty Printed JSON :", prettyJson)

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

}