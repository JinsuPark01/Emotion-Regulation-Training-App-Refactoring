package com.example.emotionalapp.ui.mind.trap

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
class TrapReportActivity : AppCompatActivity() {

    private val viewModel: TrapReportViewModel by viewModels()

    private lateinit var titleText: TextView
    private lateinit var reportSituation: TextView
    private lateinit var reportThought: TextView
    private lateinit var reportTrap: TextView
    private lateinit var reportAlternative: TextView

    private lateinit var validityContainer: View
    private lateinit var validityAnswer1: TextView
    private lateinit var validityAnswer2: TextView
    private lateinit var validityAnswer3: TextView
    private lateinit var validityAnswer4: TextView

    private lateinit var assumptionContainer: View
    private lateinit var assumptionAnswer1: TextView
    private lateinit var assumptionAnswer2: TextView
    private lateinit var assumptionAnswer3: TextView
    private lateinit var assumptionAnswer4: TextView

    private lateinit var perspectiveContainer: View
    private lateinit var perspectiveAnswer1: TextView
    private lateinit var perspectiveAnswer2: TextView
    private lateinit var perspectiveAnswer3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trap_report)

        initViews()
        observeUiState()

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        titleText = findViewById(R.id.trapReportTitleText)
        reportSituation = findViewById(R.id.reportSituation)
        reportThought = findViewById(R.id.reportThought)
        reportTrap = findViewById(R.id.reportTrap)
        reportAlternative = findViewById(R.id.reportAlternative)

        validityContainer = findViewById(R.id.validityContainer)
        validityAnswer1 = findViewById(R.id.validityAnswer1)
        validityAnswer2 = findViewById(R.id.validityAnswer2)
        validityAnswer3 = findViewById(R.id.validityAnswer3)
        validityAnswer4 = findViewById(R.id.validityAnswer4)

        assumptionContainer = findViewById(R.id.assumptionContainer)
        assumptionAnswer1 = findViewById(R.id.assumptionAnswer1)
        assumptionAnswer2 = findViewById(R.id.assumptionAnswer2)
        assumptionAnswer3 = findViewById(R.id.assumptionAnswer3)
        assumptionAnswer4 = findViewById(R.id.assumptionAnswer4)

        perspectiveContainer = findViewById(R.id.perspectiveContainer)
        perspectiveAnswer1 = findViewById(R.id.perspectiveAnswer1)
        perspectiveAnswer2 = findViewById(R.id.perspectiveAnswer2)
        perspectiveAnswer3 = findViewById(R.id.perspectiveAnswer3)
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

    private fun bindData(data: TrapReportData) {
        titleText.text = data.dateText
        reportSituation.text = data.situation
        reportThought.text = data.thought
        reportTrap.text = data.trap
        reportAlternative.text = data.alternative

        validityContainer.visibility = if (data.showValidity) View.VISIBLE else View.GONE
        if (data.showValidity) {
            validityAnswer1.text = data.validityAnswers.getOrElse(0) { "" }
            validityAnswer2.text = data.validityAnswers.getOrElse(1) { "" }
            validityAnswer3.text = data.validityAnswers.getOrElse(2) { "" }
            validityAnswer4.text = data.validityAnswers.getOrElse(3) { "" }
        }

        assumptionContainer.visibility = if (data.showAssumption) View.VISIBLE else View.GONE
        if (data.showAssumption) {
            assumptionAnswer1.text = data.assumptionAnswers.getOrElse(0) { "" }
            assumptionAnswer2.text = data.assumptionAnswers.getOrElse(1) { "" }
            assumptionAnswer3.text = data.assumptionAnswers.getOrElse(2) { "" }
            assumptionAnswer4.text = data.assumptionAnswers.getOrElse(3) { "" }
        }

        perspectiveContainer.visibility = if (data.showPerspective) View.VISIBLE else View.GONE
        if (data.showPerspective) {
            perspectiveAnswer1.text = data.perspectiveAnswers.getOrElse(0) { "" }
            perspectiveAnswer2.text = data.perspectiveAnswers.getOrElse(1) { "" }
            perspectiveAnswer3.text = data.perspectiveAnswers.getOrElse(2) { "" }
        }
    }
}