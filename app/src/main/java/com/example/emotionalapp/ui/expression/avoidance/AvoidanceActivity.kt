package com.example.emotionalapp.ui.expression.avoidance

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.util.setSingleListener
import kotlinx.coroutines.launch

class AvoidanceActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: AvoidanceViewModel by viewModels()

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
                    Toast.makeText(this@AvoidanceActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    AlertDialog.Builder(this@AvoidanceActivity)
                        .setTitle("훈련 완료!")
                        .setMessage("감정을 회피하는 습관을 돌아봤다는 것 자체가 이미 중요한 변화의 시작이에요. 스스로를 마주한 용기를 진심으로 응원해요!")
                        .setPositiveButton("확인") { _, _ ->
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

    private fun updatePage(state: AvoidanceUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0 -> "나의 회피 행동 체크하기"
            1 -> "회피 일지 작성"
            else -> "나의 회피 행동 체크하기"
        }

        val layoutId = when (state.currentPage) {
            0 -> R.layout.fragment_expression_avoidance_training_0
            1 -> R.layout.fragment_expression_avoidance_training_1
            else -> R.layout.fragment_expression_avoidance_training_0
        }

        val pageView = inflater.inflate(layoutId, pageContainer, false)
        pageContainer.addView(pageView)

        when (state.currentPage) {
            0 -> bindPage0(pageView, state)
            1 -> bindPage1(pageView, state)
        }
    }

    private fun bindPage0(pageView: View, state: AvoidanceUiState) {
        val checkBoxIds = listOf(
            R.id.cb_avoid1,
            R.id.cb_avoid2,
            R.id.cb_avoid3,
            R.id.cb_avoid4,
            R.id.cb_avoid5,
            R.id.cb_avoid6,
            R.id.cb_avoid7,
            R.id.cb_avoid8
        )

        checkBoxIds.forEachIndexed { index, id ->
            val cb = pageView.findViewById<CheckBox>(id)
            cb.isChecked = state.selectedAvoidanceIndexes.contains(index)
            cb.setOnCheckedChangeListener { _, isChecked ->
                val currentlySelected = state.selectedAvoidanceIndexes.contains(index)
                if (currentlySelected != isChecked) {
                    viewModel.toggleAvoidance(index)
                }
            }
        }

        val etCustom = pageView.findViewById<EditText>(R.id.et_custom_avoidance)
        val etEffect = pageView.findViewById<EditText>(R.id.et_effect)

        etCustom.bindAnswerTextWatcher(state.customAvoidance) {
            viewModel.updateCustomAvoidance(it)
        }

        etEffect.bindAnswerTextWatcher(state.effect) {
            viewModel.updateEffect(it)
        }
    }

    private fun bindPage1(pageView: View, state: AvoidanceUiState) {
        pageView.findViewById<EditText>(R.id.et_situation)
            .bindAnswerTextWatcher(state.situation) {
                viewModel.updateSituation(it)
            }

        pageView.findViewById<EditText>(R.id.et_emotion)
            .bindAnswerTextWatcher(state.emotion) {
                viewModel.updateEmotion(it)
            }

        pageView.findViewById<EditText>(R.id.et_method)
            .bindAnswerTextWatcher(state.method) {
                viewModel.updateMethod(it)
            }

        pageView.findViewById<EditText>(R.id.et_result)
            .bindAnswerTextWatcher(state.result) {
                viewModel.updateResult(it)
            }
    }

    private fun updateNavigationButtons(state: AvoidanceUiState) {
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
                if (i == currentPage) R.drawable.ic_dot_circle_black else R.drawable.ic_dot_circle_gray
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