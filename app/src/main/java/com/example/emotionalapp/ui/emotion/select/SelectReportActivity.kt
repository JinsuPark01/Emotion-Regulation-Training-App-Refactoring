package com.example.emotionalapp.ui.emotion.select

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectReportActivity : AppCompatActivity() {

    private lateinit var lineChartMind: LineChart
    private lateinit var lineChartBody: LineChart
    private lateinit var btnBack: ImageView

    private val viewModel: SelectReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        viewModel.loadReport()
    }

    private fun initViews() {
        lineChartMind = findViewById(R.id.lineChartMind)
        lineChartBody = findViewById(R.id.lineChartBody)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            state.errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                return@observe
            }

            state.reportData?.let { data ->
                drawLineChart(
                    chart = lineChartMind,
                    entries11 = data.mind11,
                    entries19 = data.mind19,
                    yLabels = data.mindLabels
                )

                drawLineChart(
                    chart = lineChartBody,
                    entries11 = data.body11,
                    entries19 = data.body19,
                    yLabels = data.bodyLabels
                )
            }
        }
    }

    private fun drawLineChart(
        chart: LineChart,
        entries11: List<Float?>,
        entries19: List<Float?>,
        yLabels: List<String>
    ) {
        val dataSet11 = LineDataSet(toEntries(entries11), "11시").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            setDrawValues(false)
            lineWidth = 2f
        }

        val dataSet19 = LineDataSet(toEntries(entries19), "19시").apply {
            color = Color.RED
            setCircleColor(Color.RED)
            setDrawValues(false)
            lineWidth = 2f
        }

        chart.data = LineData(dataSet11, dataSet19)
        chart.description.isEnabled = false

        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.isHighlightPerTapEnabled = false

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            axisMinimum = 0f
            axisMaximum = 6f
            valueFormatter = object : ValueFormatter() {
                private val days = listOf("1일차", "2일차", "3일차", "4일차", "5일차", "6일차", "7일차")

                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return days.getOrNull(value.toInt()) ?: value.toString()
                }
            }
        }

        chart.legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            yOffset = 24f
            xOffset = 16f
        }

        chart.axisLeft.apply {
            granularity = 1f
            axisMinimum = 0f
            axisMaximum = 4f
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return yLabels.getOrNull(value.toInt()) ?: ""
                }
            }
        }

        chart.axisRight.isEnabled = false
        chart.invalidate()
    }

    private fun toEntries(values: List<Float?>): List<Entry> {
        return values.mapIndexedNotNull { index, value ->
            value?.let { Entry(index.toFloat(), it) }
        }
    }
}