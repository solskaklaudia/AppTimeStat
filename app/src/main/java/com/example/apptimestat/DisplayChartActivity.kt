package com.example.apptimestat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
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
import org.json.JSONArray
import org.json.JSONTokener
import retrofit2.Retrofit


class DisplayChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chart)

        val timePeriod = intent.extras?.get(EXTRA_TIME)
        val textView = findViewById<TextView>(R.id.textView2)
        textView.setText(getString(R.string.chart_title) + timePeriod.toString())

        val userId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        var dateRange = 1

        when {
            timePeriod.toString() == "week" -> {
                dateRange = 7
            }
            timePeriod.toString() == "month" -> {
                dateRange = 30
            }
            timePeriod.toString() == "year" -> {
                dateRange = 365
            }
        }

        getMethod(userId, dateRange)

    }

    private fun setSliceValue(value: Float, color: Int, label: String): SliceValue {
        val context: Context = applicationContext
        return SliceValue(value, ContextCompat.getColor(context, color)).setLabel(label)
    }

    private fun getMethod(userId: String, dateRange: Int) {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apptimestat.eu.pythonanywhere.com")
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)


        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            val response = service.getData(userId, dateRange)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val colors = listOf(
                        R.color.moonstoneBlue,
                        R.color.ashGray,
                        R.color.purpleLight,
                        R.color.spanishPink,
                        R.color.pearl
                    )

                    val pieChartView = findViewById<PieChartView>(R.id.chart)
                    val pieData: MutableList<SliceValue> = ArrayList()

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.d("Pretty Printed JSON :", prettyJson)

                    val jsonArray = JSONTokener(prettyJson).nextValue() as JSONArray
                    for (i in 0 until jsonArray.length()) {
                        if (i < 5) {
                            // Average time
                            val averageTime = jsonArray.getJSONObject(i).getString("average_time")
                            Log.i("Average time: ", averageTime)

                            // Comparison with previous period
                            val comparison = jsonArray.getJSONObject(i).getString("comparison")
                            Log.i("Comparison: ", comparison)

                            // Application name
                            val name = jsonArray.getJSONObject(i).getString("name")
                            Log.i("Application name: ", name)


                            pieData += setSliceValue(averageTime.toFloat(), colors.random(), name)
                        }
                    }

                    val pieChartData = PieChartData(pieData)

                    // labels
                    pieChartData.setHasLabels(true)
                    pieChartData.valueLabelTextSize = 14

                    pieChartData.setHasCenterCircle(true)
                    pieChartData.centerText1 = "App usage time"
                    pieChartData.centerText1FontSize = 20
                    pieChartData.centerText1Color = Color.parseColor("#0097A7")

                    pieChartView.pieChartData = pieChartData

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

}