package com.example.apptimestat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView


class DisplayChartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_chart)

        val timePeriod = intent.extras?.get(EXTRA_TIME)
        val textView = findViewById<TextView>(R.id.textView2)
        textView.setText(getString(R.string.chart_title) + timePeriod.toString())

        val pieChartView = findViewById<PieChartView>(R.id.chart)
        val pieData: MutableList<SliceValue> = ArrayList()

        //pieData += SliceValue(15f, R.color.moonstoneBlue).setLabel("instagram")
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

    }

    private fun setSliceValue(value: Float, color: Int, label: String): SliceValue{
        val context: Context = applicationContext
        return SliceValue(value, ContextCompat.getColor(context, color)).setLabel(label)
    }

}