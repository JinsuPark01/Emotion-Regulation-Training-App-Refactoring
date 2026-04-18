package com.example.emotionalapp.ui.expression.opposite

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
class OppositeReportActivity : AppCompatActivity() {

    private val viewModel: OppositeReportViewModel by viewModels()

    private lateinit var btnBack: ImageView
    private lateinit var answer1: TextView
    private lateinit var answer2: TextView
    private lateinit var answer3: TextView
    private lateinit var answer5: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expression_opposite_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)
        answer5 = findViewById(R.id.answer5)
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

    private fun bindData(data: OppositeReportData) {
        answer1.text = data.answer1
        answer2.text = data.answer2
        answer3.text = data.answer3
        answer5.text = data.answer5
    }
}