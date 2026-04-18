package com.example.emotionalapp.ui.mind.art

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtReportActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var firstImage: ImageView
    private lateinit var secondImage: ImageView
    private lateinit var answerViews: List<TextView>

    private val viewModel: ArtReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_art_report)

        initViews()
        observeUiState()

        btnBack.setOnClickListener { finish() }

        val reportMillis = intent?.getLongExtra("reportDateMillis", -1L) ?: -1L
        viewModel.loadReport(reportMillis)
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        firstImage = findViewById(R.id.firstImage)
        secondImage = findViewById(R.id.secondImage)

        answerViews = listOf(
            findViewById(R.id.answer1),
            findViewById(R.id.answer2),
            findViewById(R.id.answer3),
            findViewById(R.id.answer4),
            findViewById(R.id.answer5),
            findViewById(R.id.answer6),
            findViewById(R.id.answer7),
            findViewById(R.id.answer8),
            findViewById(R.id.answer9),
            findViewById(R.id.answer10)
        )
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

            state.reportData?.let { bindDataToUi(it) }
        }
    }

    private fun bindDataToUi(data: ArtReportData) {
        firstImage.setImageResource(getResIdByImageName(data.firstImageName))
        secondImage.setImageResource(getResIdByImageName(data.secondImageName))

        for (i in 0 until 5) {
            answerViews[i].text = data.firstAnswers.getOrElse(i) { "" }
        }

        for (i in 0 until 5) {
            answerViews[i + 5].text = data.secondAnswers.getOrElse(i) { "" }
        }
    }

    private fun getResIdByImageName(name: String?): Int {
        if (name.isNullOrEmpty()) return 0
        return resources.getIdentifier(name, "drawable", packageName)
    }
}