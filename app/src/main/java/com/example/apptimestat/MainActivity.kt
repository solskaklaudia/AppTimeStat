package com.example.apptimestat

import android.app.AlertDialog
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.time.LocalDate

const val EXTRA_TIME = "com.example.apptimestat.TIME"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun displayChart(view: View) {
        val chosenTime = findViewById<Spinner>(R.id.spinner)
        val timePeriod = chosenTime.selectedItem.toString()
        val intent = Intent(this, DisplayChartActivity::class.java).apply {
            putExtra(EXTRA_TIME, timePeriod)
        }
        startActivity(intent)
    }

    fun displayTable(view: View) {
        val chosenTime = findViewById<Spinner>(R.id.spinner)
        val timePeriod = chosenTime.selectedItem.toString()
        val intent = Intent(this, DisplayTableActivity::class.java).apply {
            putExtra(EXTRA_TIME, timePeriod)

        }
        startActivity(intent)
    }

    private fun requestPermissions(): List<UsageStats> {

        val statsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val stats: List<UsageStats> = statsManager
            .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, System.currentTimeMillis())

        if (stats.isEmpty()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        return stats
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun rawJSON(view: View) {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://apptimestat.eu.pythonanywhere.com")
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Get phone id
        val userId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // Get application usage statistics
        val stats = requestPermissions()

        val apps = JSONArray()

        for (stat in stats) {
            val useTime = stat.totalTimeVisible / 60 / 1000

            if (useTime > 0) {
                val app = JSONObject()
                app.put("name", stat.packageName.substringAfterLast("."))
                app.put("use_time", useTime)
                apps.put(app)
            }
        }

        val jsonObject = JSONObject()
        jsonObject.put("user_id", userId)
        jsonObject.put("date", LocalDate.now()) // LocalDate.now()
        jsonObject.put("apps", apps)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.sendData(requestBody)
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Data sent")
        builder.setMessage("Your application usage data have been sent.")
        builder.setNeutralButton("Ok") { dialog, which ->
            Toast.makeText(
                applicationContext,
                "data sent", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()


    }
}

