package com.example.emotionalapp.ui.expression.stay

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity

class StayReportActivity : AppCompatActivity() {

    private val viewModel: StayReportViewModel by viewModels()

    private lateinit var btnBack: ImageView
    private lateinit var tvEmotion: TextView
    private lateinit var tvAnswer1: TextView
    private lateinit var tvAnswer2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expression_stay_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvEmotion = findViewById(R.id.tv_emotion)
        tvAnswer1 = findViewById(R.id.tv_answer1)
        tvAnswer2 = findViewById(R.id.tv_answer2)
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

    private fun bindData(data: StayReportData) {
        tvEmotion.text = data.emotion
        tvAnswer1.text = data.answer1
        tvAnswer2.text = data.answer2
    }
}