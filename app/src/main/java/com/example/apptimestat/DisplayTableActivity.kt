package com.example.apptimestat

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DisplayTableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_table)

        val timePeriod = intent.extras?.get(EXTRA_TIME)
        val textView = findViewById<TextView>(R.id.textView3)
        textView.setText(getString(R.string.table_title) + timePeriod.toString())
    }

}