package com.example.emotionalapp.ui.emotion

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
import com.example.emotionalapp.util.setSingleListener
import kotlinx.coroutines.launch

class AnchorActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: AnchorViewModel by viewModels()

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
                    Toast.makeText(this@AnchorActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@AnchorActivity, "닻 내리기 훈련 기록이 저장되었어요.", Toast.LENGTH_SHORT).show()
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

    private fun updatePage(state: AnchorUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0 -> "현재에 닻내리기"
            1 -> "단서 만들기"
            2 -> "정서의 3요소"
            3 -> "평가하기"
            else -> "현재에 닻내리기"
        }

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_emotion_anchor_training_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_emotion_anchor_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (state.currentPage) {
            0 -> bindIntroPage(pageView)
            1 -> bindPage1(pageView, state)
            2 -> bindPage2(pageView, state)
            3 -> bindPage3(pageView, state)
        }
    }

    private fun bindIntroPage(pageView: View) {
        val titleText0 = pageView.findViewById<TextView>(R.id.textTitleAnchor0)
        val descriptionText0 = pageView.findViewById<TextView>(R.id.textDescriptionAnchor0)
        val titleText1 = pageView.findViewById<TextView>(R.id.textTitleAnchor1)
        val descriptionText1 = pageView.findViewById<TextView>(R.id.textDescriptionAnchor1)
        val titleText2 = pageView.findViewById<TextView>(R.id.textTitleAnchor2)
        val descriptionText2 = pageView.findViewById<TextView>(R.id.textDescriptionAnchor2)

        titleText0.text = "⚓ 현재에 닻 내리기란?"
        descriptionText0.text = """
            현재에 초점을 둔 알아차림 활동이에요. 과거에 발생했던 것이나 미래에 일어날지 모를 일에 초점을 맞추는 것이 아니라 현재 맥락에서 정서반응을 일어나고 있는 그대로 관찰하는 활동입니다.
            
            강한 감정에 휩쓸릴 때, 멈추고 돌아보는 힘을 기를 수 있으며, 감정을 억누르지 않고, 있는 그대로 관찰하는 연습을 할 수 있습니다.
        """.trimIndent()

        titleText1.text = "1. 단서 선택하기 🧩"
        descriptionText1.text = """
            고통스러운 시기 동안에 현재의 순간으로 재빨리 주의를 이동하는 데 사용할 수 있는 ‘단서’를 만들어 보세요. 그 단서를 사용해 현재에 주의를 집중합니다.
        """.trimIndent()

        titleText2.text = "2. 정서의 3요소 입력하기 ✍️"
        descriptionText2.text = """
            단서를 통해 현재에 초점이 맞춰졌다면 스스로에게 다음의 세 가지 질문을 해보세요.
            
            ‘지금 나의 생각은 무엇인가?(인지)’ 
            ‘지금 내가 경험하는 정서와 신체감각은 무엇인가? (신체감각)’
            ‘나는 지금 무엇을 하고 있나? (행동)’
                
            생각, 행동이나 반응을 되돌아보며 이들을 더 적응적인 반응들로 대체해보세요. 앞으로 우리는 이 세 가지를 살펴보고 변화시키는 연습을 해 볼 거에요.
        """.trimIndent()
    }

    private fun bindPage1(pageView: View, state: AnchorUiState) {
        val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerCustom)
        val editCustomAnswer = pageView.findViewById<EditText>(R.id.editCustomAnswer)

        editCustomAnswer.bindAnswerTextWatcher(state.customCueInput) {
            viewModel.updateCustomCueInput(it)
        }

        optionContainer.removeAllViews()
        viewModel.cueOptions.forEachIndexed { index, text ->
            val card = layoutInflater.inflate(
                R.layout.item_option_card,
                optionContainer,
                false
            ) as CardView
            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = text

            if (index == state.selectedCueIndex) {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                textView.setTextColor(Color.WHITE)
            } else {
                card.setCardBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
            }

            card.setOnClickListener {
                editCustomAnswer.clearFocus()
                viewModel.selectCue(index)
            }

            optionContainer.addView(card)
        }

        editCustomAnswer.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 입력 시작하면 preset 선택 해제
                if (state.selectedCueIndex != -1) {
                    viewModel.updateCustomCueInput(editCustomAnswer.text.toString())
                }
            }
        }
    }

    private fun bindPage2(pageView: View, state: AnchorUiState) {
        val answer1 = pageView.findViewById<EditText>(R.id.answer1)
        val answer2 = pageView.findViewById<EditText>(R.id.answer2)
        val answer3 = pageView.findViewById<EditText>(R.id.answer3)

        answer1.bindAnswerTextWatcher(state.page2Answer1) {
            viewModel.updatePage2Answer1(it)
        }
        answer2.bindAnswerTextWatcher(state.page2Answer2) {
            viewModel.updatePage2Answer2(it)
        }
        answer3.bindAnswerTextWatcher(state.page2Answer3) {
            viewModel.updatePage2Answer3(it)
        }
    }

    private fun bindPage3(pageView: View, state: AnchorUiState) {
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

    private fun updateNavigationButtons(state: AnchorUiState) {
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