package com.example.apptimestat

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONTokener
import retrofit2.Retrofit

class DisplayTableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_table)

        val timePeriod = intent.extras?.get(EXTRA_TIME)
        val textView = findViewById<TextView>(R.id.textView3)
        textView.setText(getString(R.string.table_title) + timePeriod.toString())

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

    fun getMethod(userId: String, dateRange: Int) {

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


                    val name1 = findViewById<TextView>(R.id.name1)
                    val name2 = findViewById<TextView>(R.id.name2)
                    val name3 = findViewById<TextView>(R.id.name3)
                    val name4 = findViewById<TextView>(R.id.name4)
                    val name5 = findViewById<TextView>(R.id.name5)
                    val name6 = findViewById<TextView>(R.id.name6)
                    val name7 = findViewById<TextView>(R.id.name7)
                    val name8 = findViewById<TextView>(R.id.name8)
                    val name9 = findViewById<TextView>(R.id.name9)
                    val name10 = findViewById<TextView>(R.id.name10)

                    val avg1 = findViewById<TextView>(R.id.avg1)
                    val avg2 = findViewById<TextView>(R.id.avg2)
                    val avg3 = findViewById<TextView>(R.id.avg3)
                    val avg4 = findViewById<TextView>(R.id.avg4)
                    val avg5 = findViewById<TextView>(R.id.avg5)
                    val avg6 = findViewById<TextView>(R.id.avg6)
                    val avg7 = findViewById<TextView>(R.id.avg7)
                    val avg8 = findViewById<TextView>(R.id.avg8)
                    val avg9 = findViewById<TextView>(R.id.avg9)
                    val avg10 = findViewById<TextView>(R.id.avg10)

                    val comp1 = findViewById<TextView>(R.id.comp1)
                    val comp2 = findViewById<TextView>(R.id.comp2)
                    val comp3 = findViewById<TextView>(R.id.comp3)
                    val comp4 = findViewById<TextView>(R.id.comp4)
                    val comp5 = findViewById<TextView>(R.id.comp5)
                    val comp6 = findViewById<TextView>(R.id.comp6)
                    val comp7 = findViewById<TextView>(R.id.comp7)
                    val comp8 = findViewById<TextView>(R.id.comp8)
                    val comp9 = findViewById<TextView>(R.id.comp9)
                    val comp10 = findViewById<TextView>(R.id.comp10)

                    val fields = listOf(
                        listOf(name1, avg1, comp1),
                        listOf(name2, avg2, comp2),
                        listOf(name3, avg3, comp3),
                        listOf(name4, avg4, comp4),
                        listOf(name5, avg5, comp5),
                        listOf(name6, avg6, comp6),
                        listOf(name7, avg7, comp7),
                        listOf(name8, avg8, comp8),
                        listOf(name9, avg9, comp9),
                        listOf(name10, avg10, comp10)
                    )

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
                        if (i < 10) {

                            // Average time
                            val averageTime =
                                jsonArray.getJSONObject(i).getString("average_time")
                            Log.i("Average time: ", averageTime)

                            // Comparison with previous period
                            val comparison = jsonArray.getJSONObject(i).getString("comparison")
                            Log.i("Comparison: ", comparison)

                            // Application name
                            val name = jsonArray.getJSONObject(i).getString("name")
                            Log.i("Application name: ", name)

                            fields[i][0].setText(name)
                            fields[i][1].setText(averageTime)
                            fields[i][2].setText(comparison)

                        }
                    }

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

}