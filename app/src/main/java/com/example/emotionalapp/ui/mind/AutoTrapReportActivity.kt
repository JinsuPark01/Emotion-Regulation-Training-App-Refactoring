package com.example.emotionalapp.ui.mind

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class AutoTrapReportActivity : AppCompatActivity() {

    private lateinit var chart: BarChart
    private lateinit var btnBack: ImageView

    private val viewModel: AutoTrapReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_auto_trap_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        viewModel.loadReport()
    }

    private fun initViews() {
        chart = findViewById(R.id.barChart)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            state.errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                return@observe
            }

            state.reportData?.let { data ->
                drawChart(data)
            }
        }
    }

    private fun drawChart(data: AutoTrapReportData) {
        val entries = data.trapCounts.mapIndexed { index, count ->
            BarEntry(index.toFloat(), count.toFloat())
        }

        val dataSet = BarDataSet(entries, "생각의 덫 통계").apply {
            colors = listOf(
                Color.parseColor("#FF6B6B"),
                Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"),
                Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7"),
                Color.parseColor("#DDA0DD"),
                Color.parseColor("#FFB347"),
                Color.parseColor("#87CEEB"),
                Color.parseColor("#98FB98"),
                Color.parseColor("#F0E68C")
            )

            valueTextSize = 12f
            valueTextColor = Color.BLACK

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}회"
                }
            }
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.7f
        }

        chart.apply {
            this.data = barData
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            animateY(1200)

            setScaleEnabled(false)
            setPinchZoom(false)
            setDoubleTapToZoomEnabled(false)
            isDragEnabled = false
            isHighlightPerTapEnabled = false

            setExtraOffsets(20f, 30f, 20f, 50f)

            axisRight.isEnabled = false

            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                textSize = 11f
                textColor = Color.GRAY
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                gridLineWidth = 0.5f
            }

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(data.trapOptions)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setLabelCount(data.trapOptions.size, false)
                isGranularityEnabled = true
                textSize = 10f
                textColor = Color.DKGRAY
                labelRotationAngle = 0f
                setDrawGridLines(false)
                setDrawAxisLine(true)
                axisLineColor = Color.GRAY
                axisLineWidth = 1f
                yOffset = 10f
                axisMinimum = -0.5f
                axisMaximum = (data.trapOptions.size - 1).toFloat() + 0.5f
            }

            invalidate()
        }
    }
}