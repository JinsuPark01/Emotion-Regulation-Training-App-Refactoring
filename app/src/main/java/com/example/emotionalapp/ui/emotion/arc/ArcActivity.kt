package com.example.emotionalapp.ui.emotion.arc

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.emotion.arc.ArcUiState
import com.example.emotionalapp.ui.emotion.arc.ArcViewModel
import com.example.emotionalapp.util.setSingleListener
import kotlinx.coroutines.launch

class ArcActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: ArcViewModel by viewModels()

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
                    Toast.makeText(this@ArcActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@ArcActivity, "ARC 훈련 기록이 저장되었어요.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupListeners() {
        btnPrev.setOnClickListener {
            viewModel.goPrevPage()
        }

        btnNext.setSingleListener {
            val error = viewModel.validateCurrentPage()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                return@setSingleListener
            }

            if (!viewModel.isLastPage()) {
                viewModel.goNextPage()
            } else {
                btnNext.isEnabled = false
                viewModel.saveTraining()
                btnNext.isEnabled = true
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

    private fun updatePage(state: ArcUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0 -> "ARC 정서 경험 기록"
            1 -> "A: 선행 상황 혹은 사건"
            2 -> "R: 내 신체와 감정의 반응"
            3 -> "C: 단기적 결과와 장기적 결과"
            4 -> "평가하기"
            else -> "ARC 정서 경험 기록"
        }

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.fragment_emotion_arc_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_emotion_arc_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_emotion_arc_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_emotion_arc_training_3, pageContainer, false)
            4 -> inflater.inflate(R.layout.fragment_emotion_arc_training_4, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_emotion_arc_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (state.currentPage) {
            1 -> bindPage1(pageView, state)
            2 -> bindPage2(pageView, state)
            3 -> bindPage3(pageView, state)
            4 -> bindPage4(pageView, state)
        }
    }

    private fun bindPage1(pageView: View, state: ArcUiState) {
        val editSituation = pageView.findViewById<EditText>(R.id.editSituationArcA)
        editSituation.bindAnswerTextWatcher(state.userAntecedent) {
            viewModel.updateAntecedent(it)
        }
    }

    private fun bindPage2(pageView: View, state: ArcUiState) {
        val editReaction = pageView.findViewById<EditText>(R.id.editReactionArcR)
        editReaction.bindAnswerTextWatcher(state.userResponse) {
            viewModel.updateResponse(it)
        }
    }

    private fun bindPage3(pageView: View, state: ArcUiState) {
        val editShortTerm = pageView.findViewById<EditText>(R.id.editShortTermArcC)
        val editLongTerm = pageView.findViewById<EditText>(R.id.editLongTermArcC)

        editShortTerm.bindAnswerTextWatcher(state.userShortConsequence) {
            viewModel.updateShortConsequence(it)
        }

        editLongTerm.bindAnswerTextWatcher(state.userLongConsequence) {
            viewModel.updateLongConsequence(it)
        }
    }

    private fun bindPage4(pageView: View, state: ArcUiState) {
        val optionContainerQ1 = pageView.findViewById<LinearLayout>(R.id.optionContainerQ1)
        val optionContainerQ2 = pageView.findViewById<LinearLayout>(R.id.optionContainerQ2)

        optionContainerQ1.removeAllViews()
        optionContainerQ2.removeAllViews()

        viewModel.optionsQ1.forEachIndexed { index, text ->
            val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerQ1, false) as CardView
            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = text

            if (index == state.selectedQ1Index) {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                textView.setTextColor(Color.WHITE)
            } else {
                card.setCardBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
            }

            card.setOnClickListener {
                viewModel.selectQ1(index)
            }

            optionContainerQ1.addView(card)
        }

        viewModel.optionsQ2.forEachIndexed { index, text ->
            val card = layoutInflater.inflate(R.layout.item_option_card, optionContainerQ2, false) as CardView
            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = text

            if (index == state.selectedQ2Index) {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                textView.setTextColor(Color.WHITE)
            } else {
                card.setCardBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
            }

            card.setOnClickListener {
                viewModel.selectQ2(index)
            }

            optionContainerQ2.addView(card)
        }
    }

    private fun updateNavigationButtons(state: ArcUiState) {
        val currentPage = state.currentPage

        btnPrev.isEnabled = currentPage != 0
        btnPrev.backgroundTintList = if (currentPage == 0) {
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

    private fun EditText.bindAnswerTextWatcher(
        initialText: String,
        onTextChanged: (String) -> Unit
    ) {
        if (text.toString() != initialText) {
            setText(initialText)
            setSelection(text.length)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                onTextChanged(s?.toString().orEmpty())
            }
        }

        addTextChangedListener(watcher)
    }
}