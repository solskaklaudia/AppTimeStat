package com.example.apptimestat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

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
}