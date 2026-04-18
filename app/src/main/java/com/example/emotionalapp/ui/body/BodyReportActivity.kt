package com.example.emotionalapp.ui.body

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BodyReportActivity : AppCompatActivity() {

    private val viewModel: BodyReportViewModel by viewModels()

    private lateinit var btnBack: ImageView
    private lateinit var tvTrainingTitle: TextView
    private lateinit var tvReportDate: TextView
    private lateinit var tvContent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_report_detail)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        val reportDateMillis = intent.getLongExtra("reportDateMillis", -1L)
        val trainingId = intent.getStringExtra("trainingId") ?: ""

        viewModel.loadReport(reportDateMillis, trainingId)
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTrainingTitle = findViewById(R.id.tvTrainingTitle)
        tvReportDate = findViewById(R.id.tvReportDate)
        tvContent = findViewById(R.id.tvContent)
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (state.reportData == null) {
                    finish()
                }
                return@observe
            }

            state.reportData?.let { bindData(it) }
        }
    }

    private fun bindData(data: BodyReportData) {
        tvTrainingTitle.text = data.trainingTitle
        tvReportDate.text = data.reportDateText
        tvContent.text = data.content
    }
}