package com.example.emotionalapp.ui.weekly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeeklyReportActivity : AppCompatActivity() {

    private val viewModel: WeeklyReportViewModel by viewModels()

    private lateinit var titleText: TextView
    private lateinit var phq9Score: TextView
    private lateinit var phq9Interpretation: TextView
    private lateinit var gad7Score: TextView
    private lateinit var gad7Interpretation: TextView
    private lateinit var panasPositiveScore: TextView
    private lateinit var panasNegativeScore: TextView
    private lateinit var panasInterpretation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_report)

        initViews()
        observeUiState()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        titleText = findViewById(R.id.weeklyReportTitleText)
        phq9Score = findViewById(R.id.phq9Score)
        phq9Interpretation = findViewById(R.id.phq9Interpretation)
        gad7Score = findViewById(R.id.gad7Score)
        gad7Interpretation = findViewById(R.id.gad7Interpretation)
        panasPositiveScore = findViewById(R.id.panasPositiveScore)
        panasNegativeScore = findViewById(R.id.panasNegativeScore)
        panasInterpretation = findViewById(R.id.panasInterpretation)
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { state ->
            if (state.shouldNavigateToLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return@observe
            }

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

    private fun bindData(data: WeeklyReportData) {
        titleText.text = data.dateText

        val hasMissingScore = data.phq9Sum == -1 ||
                data.gad7Sum == -1 ||
                data.positiveSum == -1 ||
                data.negativeSum == -1

        if (hasMissingScore) {
            phq9Score.text = "점수: 기록되지 않음"
            phq9Interpretation.text = viewModel.interpretPhq9(data.phq9Sum)

            gad7Score.text = "점수: 기록되지 않음"
            gad7Interpretation.text = viewModel.interpretGad7(data.gad7Sum)

            panasPositiveScore.text = "긍정 점수: 기록되지 않음"
            panasNegativeScore.text = "부정 점수: 기록되지 않음"
            panasInterpretation.text = viewModel.interpretPanas(data.positiveSum, data.negativeSum)
        } else {
            phq9Score.text = "점수: ${data.phq9Sum}점"
            phq9Interpretation.text = viewModel.interpretPhq9(data.phq9Sum)

            gad7Score.text = "점수: ${data.gad7Sum}점"
            gad7Interpretation.text = viewModel.interpretGad7(data.gad7Sum)

            panasPositiveScore.text = "긍정 점수: ${data.positiveSum} (평균: 29 ~ 34)"
            panasNegativeScore.text = "부정 점수: ${data.negativeSum} (평균: 26 ~ 30)"
            panasInterpretation.text = viewModel.interpretPanas(data.positiveSum, data.negativeSum)
        }
    }
}