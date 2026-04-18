package com.example.emotionalapp.ui.mind.auto

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.emotionalapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AutoReportActivity : AppCompatActivity() {

    private lateinit var answer1: TextView
    private lateinit var answer2: TextView
    private lateinit var answer3: TextView
    private lateinit var answer5: TextView
    private lateinit var optionContainerTrap2: LinearLayout
    private lateinit var btnBack: ImageView

    private val viewModel: AutoReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_auto_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        val reportDateMillis = intent.getLongExtra("reportDateMillis", -1L)
        viewModel.loadReport(reportDateMillis)
    }

    private fun initViews() {
        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)
        answer5 = findViewById(R.id.answer5)
        optionContainerTrap2 = findViewById(R.id.optionContainerTrap2)
        btnBack = findViewById(R.id.btnBack)
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

    private fun bindData(data: AutoReportData) {
        answer1.text = data.answer1
        answer2.text = data.answer2
        answer3.text = data.answer3
        answer5.text = data.answer5
        showTrapCard(data.trap)
    }

    private fun showTrapCard(trap: String) {
        optionContainerTrap2.removeAllViews()

        if (trap.isNotBlank()) {
            val card = layoutInflater.inflate(
                R.layout.item_option_card,
                optionContainerTrap2,
                false
            ) as CardView

            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = "-$trap"
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            optionContainerTrap2.addView(card)
        }
    }
}