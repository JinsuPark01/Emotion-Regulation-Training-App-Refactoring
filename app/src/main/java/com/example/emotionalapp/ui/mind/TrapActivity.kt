package com.example.emotionalapp.ui.mind

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
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

class TrapActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: TrapViewModel by viewModels()

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
                    Toast.makeText(this@TrapActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@TrapActivity, "생각의 덫이 기록되었습니다.", Toast.LENGTH_SHORT).show()
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

            if (viewModel.shouldSaveAtCurrentPage()) {
                btnNext.isEnabled = false
                viewModel.saveTraining()
                btnNext.isEnabled = true
                return@setSingleListener
            }

            val shouldFinish = viewModel.moveToNextPageOrFinish()
            if (shouldFinish) {
                finish()
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

    private fun updatePage(state: TrapUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0 -> "자동적 사고와 생각의 덫"
            1 -> "생각의 덫 파악하기"
            2 -> "생각의 덫 파악하기"
            3 -> "생각의 덫 풀어내기"
            4 -> "생각의 덫 풀어내기"
            5 -> "생각의 덫 풀어내기"
            6 -> "생각의 덫 풀어내기"
            7 -> "생각의 덫 풀어내기"
            else -> "생각의 덫"
        }

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_trap_training_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_trap_training_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_trap_training_2, pageContainer, false)
            3 -> inflater.inflate(R.layout.fragment_mind_trap_training_3, pageContainer, false)
            4 -> {
                when (state.selectedTrapIndex) {
                    0 -> inflater.inflate(R.layout.fragment_mind_trap_training_4_0, pageContainer, false)
                    1 -> inflater.inflate(R.layout.fragment_mind_trap_training_4_1, pageContainer, false)
                    2 -> inflater.inflate(R.layout.fragment_mind_trap_training_4_2, pageContainer, false)
                    else -> throw IllegalStateException("선택된 옵션이 없습니다.")
                }
            }
            5 -> inflater.inflate(R.layout.fragment_mind_trap_training_5, pageContainer, false)
            6 -> inflater.inflate(R.layout.fragment_mind_trap_training_6, pageContainer, false)
            7 -> inflater.inflate(R.layout.fragment_mind_trap_training_0, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_trap_training_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (state.currentPage) {
            0 -> bindIntroPage(pageView)
            1 -> bindPage1(pageView, state)
            2 -> bindPage2(pageView, state)
            3 -> bindPage3(pageView, state)
            4 -> bindPage4(pageView, state)
            5 -> bindPage5(pageView)
            6 -> bindPage6(pageView, state)
            7 -> bindFinishPage(pageView)
        }
    }

    private fun bindIntroPage(pageView: View) {
        val title = pageView.findViewById<TextView>(R.id.textTitleTrap0)
        val description = pageView.findViewById<TextView>(R.id.textDescriptionTrap0)

        title.text = "오늘의 훈련"
        description.text = """
            🧠 자동적으로 떠오르는 생각이 있어요
            우리가 어떤 사건이나 상황을 겪을 때, 아주 빠르게 스쳐 지나가는 생각들이 있는데
            이걸 ‘자동적 평가’ 혹은 ‘자동적 사고’라고 해요.
            
            ⏳ 시간이 지나면서
            사람마다 상황을 해석하는 고유한 방식(스타일)이 만들어져요.
            이 덕분에 우리는 정보를 빠르고 효율적으로 처리할 수 있지만,
            그 안에 가끔은 비합리적이거나 왜곡된 ‘생각의 덫(thinking traps)’이 섞여 있을 수 있어요.
            
            ⚠️ 중요한 건
            이런 생각들이 “나쁘다/틀렸다”는 게 아니라,
            그 생각이 우리 시야를 좁혀서
            다른 해석 가능성을 보지 못하게 만들 때 문제가 된다는 거예요.
            
            🛠 그래서 이번 모듈에서는
            대표적인 생각의 덫들을 배우고,
            그중에서 내가 특히 자주 빠지는 패턴이 무엇인지
            ‘알아차리는 연습’을 해볼 거예요.
        """.trimIndent()
    }

    private fun bindPage1(pageView: View, state: TrapUiState) {
        val answer1 = pageView.findViewById<EditText>(R.id.answer1)
        val answer2 = pageView.findViewById<EditText>(R.id.answer2)

        answer1.bindAnswerTextWatcher(
            initialText = state.responsePage1Answer1
        ) { newText ->
            viewModel.updatePage1Answers(
                answer1 = newText,
                answer2 = answer2.text.toString()
            )
        }

        answer2.bindAnswerTextWatcher(
            initialText = state.responsePage1Answer2
        ) { newText ->
            viewModel.updatePage1Answers(
                answer1 = answer1.text.toString(),
                answer2 = newText
            )
        }
    }

    private fun bindPage2(pageView: View, state: TrapUiState) {
        val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerTrap2)

        viewModel.page3Options // no-op to keep symmetry unused safe

        val options = listOf(
            "성급하게 결론짓기\n -이 비행기가 추락할 확률은 90%야. (실제 확률은 0.000013%)",
            "최악을 생각하기\n -부모님이 집에 늦게 들어오시네. 사고를 당한 것 같아.",
            "긍정적인 면 무시하기\n -시험문제가 우연히 쉬워서 좋은 점수를 받았을 뿐이야.",
            "흑백사고\n -시험에서 100점을 받지 못한다면 나는 실패자야.",
            "점쟁이 사고 (지레짐작하기)\n -연주회를 망칠 거야, 공연을 하지 않겠어.",
            "독심술\n -한 번도 대화를 나누지는 않았지만, 쟤는 나를 좋아하지 않아.",
            "정서적 추리\n -애인이 일 때문에 늦는다고 했지만, 그게 아닌 것 같아. 직감이 와. 나를 속이는 게 틀림없어.",
            "꼬리표 붙이기\n -나는 멍청해.",
            "“해야만 한다“는 진술문\n -사람들은 모두 정직해야해. 거짓말을 하는 건 있을 수 없는 일이야.",
            "마술적 사고\n -내가 아버지에게 전화를 걸면 아버지는 사고를 피할 수 있을 거야."
        )

        options.forEachIndexed { index, text ->
            val card = layoutInflater.inflate(R.layout.item_option_card, optionContainer, false) as CardView
            val textView = card.findViewById<TextView>(R.id.textOption)
            textView.text = text

            if (index == state.selectedPage2Index) {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray))
                textView.setTextColor(Color.WHITE)
            } else {
                card.setCardBackgroundColor(Color.WHITE)
                textView.setTextColor(Color.BLACK)
            }

            card.setOnClickListener {
                viewModel.selectPage2Option(index)
            }

            optionContainer.addView(card)
        }
    }

    private fun bindPage3(pageView: View, state: TrapUiState) {
        val optionContainer = pageView.findViewById<LinearLayout>(R.id.optionContainerTrap2)

        viewModel.page3Options.forEachIndexed { index, text ->
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
                viewModel.selectTrapOption(index)
            }

            optionContainer.addView(card)
        }
    }

    private fun bindPage4(pageView: View, state: TrapUiState) {
        val answer1 = pageView.findViewById<EditText>(R.id.answer1)
        val answer2 = pageView.findViewById<EditText>(R.id.answer2)
        val answer3 = pageView.findViewById<EditText>(R.id.answer3)
        val answer4 = pageView.findViewById<EditText?>(R.id.answer4)

        when (state.selectedTrapIndex) {
            0 -> {
                answer1.bindAnswerTextWatcher(state.responsePage4ZeroAnswers[0]) {
                    viewModel.updatePage4Answer(0, it)
                }
                answer2.bindAnswerTextWatcher(state.responsePage4ZeroAnswers[1]) {
                    viewModel.updatePage4Answer(1, it)
                }
                answer3.bindAnswerTextWatcher(state.responsePage4ZeroAnswers[2]) {
                    viewModel.updatePage4Answer(2, it)
                }
                answer4?.bindAnswerTextWatcher(state.responsePage4ZeroAnswers[3]) {
                    viewModel.updatePage4Answer(3, it)
                }
            }

            1 -> {
                answer1.bindAnswerTextWatcher(state.responsePage4OneAnswers[0]) {
                    viewModel.updatePage4Answer(0, it)
                }
                answer2.bindAnswerTextWatcher(state.responsePage4OneAnswers[1]) {
                    viewModel.updatePage4Answer(1, it)
                }
                answer3.bindAnswerTextWatcher(state.responsePage4OneAnswers[2]) {
                    viewModel.updatePage4Answer(2, it)
                }
                answer4?.bindAnswerTextWatcher(state.responsePage4OneAnswers[3]) {
                    viewModel.updatePage4Answer(3, it)
                }
            }

            2 -> {
                answer1.bindAnswerTextWatcher(state.responsePage4TwoAnswers[0]) {
                    viewModel.updatePage4Answer(0, it)
                }
                answer2.bindAnswerTextWatcher(state.responsePage4TwoAnswers[1]) {
                    viewModel.updatePage4Answer(1, it)
                }
                answer3.bindAnswerTextWatcher(state.responsePage4TwoAnswers[2]) {
                    viewModel.updatePage4Answer(2, it)
                }
            }
        }
    }

    private fun bindPage5(pageView: View) {
        val btnGoBack = pageView.findViewById<Button>(R.id.btnGoBackTrap5)
        val btnContinue = pageView.findViewById<Button>(R.id.btnContinueTrap5)

        btnGoBack.setOnClickListener {
            viewModel.goToPage(3)
        }

        btnContinue.setOnClickListener {
            viewModel.goToPage(6)
        }
    }

    private fun bindPage6(pageView: View, state: TrapUiState) {
        val answer1 = pageView.findViewById<EditText>(R.id.answer1)
        answer1.bindAnswerTextWatcher(
            initialText = state.responsePage6Text
        ) {
            viewModel.updatePage6Answer(it)
        }
    }

    private fun bindFinishPage(pageView: View) {
        val title = pageView.findViewById<TextView>(R.id.textTitleTrap0)
        val description = pageView.findViewById<TextView>(R.id.textDescriptionTrap0)

        title.text = "대단해요!"
        description.text =
            "🧠 생각의 덫은 유연성을 낮추고, 여러 가지 다양한 해석을 못하게 할 수 있어요. " +
                    "자동적 평가는 ‘나쁘거나’ ‘잘못된’ 사고방식이기 때문이 아니라, 주어진 상황에 관한 해석을 제한하기 때문에 문제가 됩니다. " +
                    "따라서 우리의 목표는 상황을 평가하는 데 있어 나쁜 생각을 대체하거나 잘못된 사고방식을 ‘고치는’ 것이 아니라, 유연성을 키우는 것입니다.\n\n" +
                    "🔓 이러한 생각의 덫에서 벗어나기 위해서는 자동적 평가를 ‘객관적 사실‘이 아니라, 그 상황에 관한 가능한 해석으로 고려해야 합니다. " +
                    "최악의 시나리오는 여전히 떠오를 수 있지만, 그 상황에 대해 할 수 있는 다른 평가들과 ‘공존’할 수 있어요. " +
                    "우리의 목표는 생각을 유연하게 하고 정서를 유발하는 상황에서 여러 대안적 평가를 내릴 수 있도록 하는 것입니다."
    }

    private fun updateNavigationButtons(state: TrapUiState) {
        val currentPage = state.currentPage

        btnPrev.isEnabled = !(currentPage == 0 || currentPage == 5)
        btnPrev.backgroundTintList = if (currentPage == 0 || currentPage == 5) {
            ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
        } else {
            ColorStateList.valueOf(Color.parseColor("#00897B"))
        }

        btnNext.isEnabled = currentPage != 5
        btnNext.backgroundTintList = if (currentPage == 5) {
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