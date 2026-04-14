package com.example.emotionalapp.ui.emotion.anchor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity

class AnchorReportActivity : AppCompatActivity() {

    private val viewModel: AnchorReportViewModel by viewModels()

    private lateinit var titleText: TextView
    private lateinit var reportTextC: TextView
    private lateinit var reportTextT: TextView
    private lateinit var reportTextS: TextView
    private lateinit var reportTextB: TextView
    private lateinit var reportTextEC: TextView
    private lateinit var reportTextEE: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anchor_report)

        initViews()
        observeUiState()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        titleText = findViewById(R.id.anchorReportTitleText)
        reportTextC = findViewById(R.id.reportTextC)
        reportTextT = findViewById(R.id.reportTextT)
        reportTextS = findViewById(R.id.reportTextS)
        reportTextB = findViewById(R.id.reportTextB)
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

    private fun bindData(data: AnchorReportData) {
        titleText.text = data.dateText
        reportTextC.text = data.selectedCue
        reportTextT.text = data.thought
        reportTextS.text = data.sensation
        reportTextB.text = data.behavior
        reportTextEC.text = data.change
        reportTextEE.text = data.effect
    }
}