package com.example.emotionalapp.ui.emotion.arc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity

class ArcReportActivity : AppCompatActivity() {

    private val viewModel: ArcReportViewModel by viewModels()

    private lateinit var titleText: TextView
    private lateinit var reportTextA: TextView
    private lateinit var reportTextR: TextView
    private lateinit var reportTextSC: TextView
    private lateinit var reportTextLC: TextView
    private lateinit var reportTextEC: TextView
    private lateinit var reportTextEE: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arc_report)

        initViews()
        observeUiState()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        titleText = findViewById(R.id.arcReportTitleText)
        reportTextA = findViewById(R.id.reportTextA)
        reportTextR = findViewById(R.id.reportTextR)
        reportTextSC = findViewById(R.id.reportTextSC)
        reportTextLC = findViewById(R.id.reportTextLC)
        reportTextEC = findViewById(R.id.reportTextEC)
        reportTextEE = findViewById(R.id.reportTextEE)
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

    private fun bindData(data: ArcReportData) {
        titleText.text = data.dateText
        reportTextA.text = data.antecedent
        reportTextR.text = data.response
        reportTextSC.text = data.shortConsequence
        reportTextLC.text = data.longConsequence
        reportTextEC.text = data.change
        reportTextEE.text = data.effect
    }
}