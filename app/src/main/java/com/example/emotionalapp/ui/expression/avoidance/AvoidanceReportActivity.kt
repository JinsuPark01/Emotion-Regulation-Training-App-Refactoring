package com.example.emotionalapp.ui.expression.avoidance

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.login_signup.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvoidanceReportActivity : AppCompatActivity() {

    private val viewModel: AvoidanceReportViewModel by viewModels()

    private lateinit var btnBack: ImageView
    private lateinit var tvAvoid1: TextView
    private lateinit var tvAvoid2: TextView
    private lateinit var tvAnswer1: TextView
    private lateinit var tvAnswer2: TextView
    private lateinit var tvAnswer3: TextView
    private lateinit var tvResult4: TextView
    private lateinit var tvEffect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expression_avoidance_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvAvoid1 = findViewById(R.id.tv_avoid1)
        tvAvoid2 = findViewById(R.id.tv_avoid2)
        tvAnswer1 = findViewById(R.id.tv_answer1)
        tvAnswer2 = findViewById(R.id.tv_answer2)
        tvAnswer3 = findViewById(R.id.tv_answer3)
        tvResult4 = findViewById(R.id.tv_result4)
        tvEffect = findViewById(R.id.tv_effect)
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

    private fun bindData(data: AvoidanceReportData) {
        tvAvoid1.text = data.avoid1
        tvAvoid2.text = data.avoid2
        tvAnswer1.text = data.answer1
        tvAnswer2.text = data.answer2
        tvAnswer3.text = data.answer3
        tvResult4.text = data.result4
        tvEffect.text = data.effect
    }
}