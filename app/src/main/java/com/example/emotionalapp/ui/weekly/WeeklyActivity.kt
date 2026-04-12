package com.example.emotionalapp.ui.weekly

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import kotlinx.coroutines.launch

class WeeklyActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private lateinit var phq9ButtonGroups: List<List<LinearLayout>>
    private lateinit var gad7ButtonGroups: List<List<LinearLayout>>
    private lateinit var panasButtonGroups: List<List<LinearLayout>>

    private val viewModel: WeeklyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_frame)

        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        indicatorContainer = findViewById(R.id.indicatorContainer)
        pageContainer = findViewById(R.id.pageContainer)
        titleText = findViewById(R.id.titleText)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        setupIndicators(viewModel.uiState.value.totalPages)
        observeUiState()
        setupListeners()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updatePage(state)
                updateNavigationButtons(state)

                state.errorMessage?.let {
                    Toast.makeText(this@WeeklyActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    viewModel.goNextPage()
                }
            }
        }
    }

    private fun setupListeners() {
        btnPrev.setOnClickListener {
            viewModel.goPrevPage()
        }

        btnNext.setOnClickListener {
            if (viewModel.isLastPage()) {
                Toast.makeText(this@WeeklyActivity, "주차별 점검이 기록되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            val error = viewModel.validateCurrentPage()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.calculateCurrentPageScores()

            if (viewModel.shouldSaveAtCurrentPage()) {
                btnNext.isEnabled = false
                viewModel.saveTraining()
                btnNext.isEnabled = true
            } else {
                viewModel.goNextPage()
            }
        }
    }

    private fun setupIndicators(count: Int) {
        indicatorContainer.removeAllViews()
        repeat(count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    marginStart = 8
                    marginEnd = 8
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updatePage(state: WeeklyUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0 -> "PHQ-9"
            1 -> "GAD-7"
            2 -> "PANAS"
            3 -> "결과"
            else -> "사전 테스트"
        }

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.fragment_phq9_training, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_gad7_training, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_panas_training, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_weekly_result, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_phq9_training, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (state.currentPage) {
            0 -> bindPhq9Page(pageView, state)
            1 -> bindGad7Page(pageView, state)
            2 -> bindPanasPage(pageView, state)
            3 -> bindResultPage(pageView, state)
        }
    }

    private fun bindPhq9Page(pageView: View, state: WeeklyUiState) {
        phq9ButtonGroups = List(9) { questionIndex ->
            List(4) { optionIndex ->
                val resId = resources.getIdentifier(
                    "btn${questionIndex}_${optionIndex}",
                    "id",
                    packageName
                )
                pageView.findViewById<LinearLayout>(resId)
            }
        }

        phq9ButtonGroups.forEachIndexed { questionIndex, buttonGroup ->
            buttonGroup.forEachIndexed { optionIndex, button ->
                button.setOnClickListener {
                    viewModel.selectPhq9(questionIndex, optionIndex)
                }
            }
        }

        state.phq9Selections.forEachIndexed { questionIndex, selectedIndex ->
            if (selectedIndex != -1) {
                updateButtonState(phq9ButtonGroups[questionIndex], selectedIndex)
            }
        }
    }

    private fun bindGad7Page(pageView: View, state: WeeklyUiState) {
        gad7ButtonGroups = List(7) { questionIndex ->
            List(4) { optionIndex ->
                val resId = resources.getIdentifier(
                    "btnG${questionIndex}_${optionIndex}",
                    "id",
                    packageName
                )
                pageView.findViewById<LinearLayout>(resId)
            }
        }

        gad7ButtonGroups.forEachIndexed { questionIndex, buttonGroup ->
            buttonGroup.forEachIndexed { optionIndex, button ->
                button.setOnClickListener {
                    viewModel.selectGad7(questionIndex, optionIndex)
                }
            }
        }

        state.gad7Selections.forEachIndexed { questionIndex, selectedIndex ->
            if (selectedIndex != -1) {
                updateButtonState(gad7ButtonGroups[questionIndex], selectedIndex)
            }
        }
    }

    private fun bindPanasPage(pageView: View, state: WeeklyUiState) {
        panasButtonGroups = List(20) { questionIndex ->
            List(5) { optionIndex ->
                val resId = resources.getIdentifier(
                    "btnP${questionIndex}_${optionIndex}",
                    "id",
                    packageName
                )
                pageView.findViewById<LinearLayout>(resId)
            }
        }

        panasButtonGroups.forEachIndexed { questionIndex, buttonGroup ->
            buttonGroup.forEachIndexed { optionIndex, button ->
                button.setOnClickListener {
                    viewModel.selectPanas(questionIndex, optionIndex)
                }
            }
        }

        state.panasSelections.forEachIndexed { questionIndex, selectedIndex ->
            if (selectedIndex != -1) {
                updateButtonState(panasButtonGroups[questionIndex], selectedIndex)
            }
        }
    }

    private fun bindResultPage(pageView: View, state: WeeklyUiState) {
        pageView.findViewById<TextView>(R.id.phq9Score).text = "점수: ${state.phq9Sum}점"
        pageView.findViewById<TextView>(R.id.phq9Interpretation).text =
            viewModel.interpretPhq9(state.phq9Sum)

        pageView.findViewById<TextView>(R.id.gad7Score).text = "점수: ${state.gad7Sum}점"
        pageView.findViewById<TextView>(R.id.gad7Interpretation).text =
            viewModel.interpretGad7(state.gad7Sum)

        pageView.findViewById<TextView>(R.id.panasPositiveScore).text =
            "긍정 점수: ${state.panasPositiveSum} (평균: 29 ~ 34)"
        pageView.findViewById<TextView>(R.id.panasNegativeScore).text =
            "부정 점수: ${state.panasNegativeSum} (평균: 26 ~ 30)"
        pageView.findViewById<TextView>(R.id.panasInterpretation).text =
            viewModel.interpretPanas(state.panasPositiveSum, state.panasNegativeSum)
    }

    private fun updateButtonState(
        buttonGroup: List<LinearLayout>,
        selectedIndex: Int
    ) {
        buttonGroup.forEachIndexed { index, btn ->
            val iconImageView = btn.getChildAt(0) as ImageView
            val labelTextView = btn.getChildAt(1) as TextView

            if (index == selectedIndex) {
                btn.alpha = 1.0f
                iconImageView.setImageResource(R.drawable.ic_weekly_on)
                labelTextView.setTextColor(Color.parseColor("#00897B"))
            } else {
                btn.alpha = 0.3f
                iconImageView.setImageResource(R.drawable.ic_weekly)
                labelTextView.setTextColor(Color.parseColor("#000000"))
            }
        }
    }

    private fun updateNavigationButtons(state: WeeklyUiState) {
        val currentPage = state.currentPage

        btnPrev.isEnabled = currentPage != 0 && currentPage != 3
        btnPrev.backgroundTintList = if (currentPage == 0 || currentPage == 3) {
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        } else {
            ColorStateList.valueOf(Color.parseColor("#00897B"))
        }

        btnNext.text = if (currentPage == state.totalPages - 1) "완료 →" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == currentPage) R.drawable.ic_dot_circle_black
                else R.drawable.ic_dot_circle_gray
            )
        }
    }
}