package com.example.emotionalapp.ui.mind

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

class AutoActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: AutoViewModel by viewModels()

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
                    Toast.makeText(this@AutoActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    AlertDialog.Builder(this@AutoActivity)
                        .setTitle("수고 많으셨습니다.")
                        .setMessage(
                            "🌿 우리는 누구나 익숙한 방식으로 세상을 바라보며 살아갑니다. " +
                                    "여러분은 그 익숙함을 잠시 멈추고, 다른 시선과 해석의 가능성을 연습하셨습니다. " +
                                    "물론 훈련이 끝났다고 해서 완벽하게 새로운 해석이 바로 떠오르지 않을 수 있어요. " +
                                    "중요한 것은 해석을 바꿀 수 있다는 ‘가능성’을 기억하는 것입니다.\n\n" +
                                    "🪞 앞으로도 감정을 흔들리게 하는 생각이 떠오를 때,\n" +
                                    "“내가 지금 어떻게 해석하고 있는 걸까?”,\n" +
                                    "“혹시 다른 해석도 가능하지 않을까?” 라는 질문을 마음속에 떠올려보세요.\n\n" +
                                    "💡 감정은 우리가 세상을 어떻게 해석하는지에 따라 달라집니다. " +
                                    "그리고 해석은 언제든지 다시 바라보고, 선택할 수 있는 것입니다. " +
                                    "앞으로도 연습을 통해 생각의 범위를 더욱 늘려가봅시다. 🌱"
                        )
                        .setPositiveButton("확인") { _, _ ->
                            viewModel.consumeSaveSuccess()
                            finish()
                        }
                        .setCancelable(false)
                        .show()
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

            btnNext.isEnabled = false

            if (!viewModel.isLastPage()) {
                viewModel.goNextPage()
                btnNext.isEnabled = true
            } else {
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

    private fun updatePage(state: AutoUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0 -> "자동적 평가"
            1 -> "자동적 사고 평가하기"
            2 -> "생각의 덫 목록"
            3 -> "자동적 사고 평가하기"
            else -> "자동적 평가"
        }

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_auto_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_auto_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_auto_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_mind_auto_training_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_auto_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (state.currentPage) {
            1 -> bindPage1(pageView, state)
            2 -> bindPage2(pageView, state)
            3 -> bindPage3(pageView, state)
        }
    }

    private fun bindPage1(pageView: View, state: AutoUiState) {
        val a1 = pageView.findViewById<EditText>(R.id.answer1)
        val a2 = pageView.findViewById<EditText>(R.id.answer2)
        val a3 = pageView.findViewById<EditText>(R.id.answer3)

        a1.bindAnswerTextWatcher(state.answerList[0]) {
            viewModel.updateAnswer(0, it)
        }
        a2.bindAnswerTextWatcher(state.answerList[1]) {
            viewModel.updateAnswer(1, it)
        }
        a3.bindAnswerTextWatcher(state.answerList[2]) {
            viewModel.updateAnswer(2, it)
        }
    }

    private fun bindPage2(pageView: View, state: AutoUiState) {
        val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerTrap2)
        optionContainer.removeAllViews()

        viewModel.trapOptions.forEachIndexed { index, text ->
            val card = layoutInflater.inflate(R.layout.item_option_card, optionContainer, false) as CardView
            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = text

            if (index == state.selectedTrapIndex) {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                textView.setTextColor(Color.WHITE)
            } else {
                card.setCardBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
            }

            card.setOnClickListener {
                viewModel.selectTrap(index)
            }

            optionContainer.addView(card)
        }
    }

    private fun bindPage3(pageView: View, state: AutoUiState) {
        val a5 = pageView.findViewById<EditText>(R.id.answer5)
        val accordionHeader = pageView.findViewById<LinearLayout>(R.id.accordionQuestionList)
        val descView = pageView.findViewById<TextView>(R.id.tvQuestionListDesc)

        a5.bindAnswerTextWatcher(state.answerList[4]) {
            viewModel.updateAnswer(4, it)
        }

        accordionHeader?.setOnClickListener {
            descView.visibility = if (descView.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun updateNavigationButtons(state: AutoUiState) {
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