package com.example.emotionalapp.ui.expression.opposite

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.databinding.ActivityOppositeActionBinding
import com.example.emotionalapp.util.setSingleListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class OppositeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOppositeActionBinding
    private val viewModel: OppositeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOppositeActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIndicators()
        observeUiState()
        setupListeners()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updatePage(state)

                state.errorMessage?.let {
                    Toast.makeText(this@OppositeActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(
                        this@OppositeActivity,
                        "반대 행동 하기 기록이 저장되었습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        binding.navPage.btnNext.setSingleListener {
            val error = viewModel.validateCurrentPage()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                return@setSingleListener
            }

            if (!viewModel.isLastPage()) {
                viewModel.goNextPage()
            } else {
                binding.navPage.btnNext.isEnabled = false
                viewModel.saveTraining()
                binding.navPage.btnNext.isEnabled = true
            }
        }

        binding.navPage.btnPrev.setOnClickListener {
            viewModel.goPrevPage()
        }
    }

    private fun updatePage(state: OppositeUiState) {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.page_opposite_action_1_guide, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_opposite_action_2_record, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_opposite_action_3_final, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page number")
        }

        binding.pageContainer.addView(pageView)

        if (state.currentPage == 1) {
            pageView.findViewById<EditText>(R.id.answer1)
                .bindAnswerTextWatcher(state.answer1) {
                    viewModel.updateAnswer1(it)
                }

            pageView.findViewById<EditText>(R.id.answer2)
                .bindAnswerTextWatcher(state.answer2) {
                    viewModel.updateAnswer2(it)
                }

            pageView.findViewById<EditText>(R.id.answer3)
                .bindAnswerTextWatcher(state.answer3) {
                    viewModel.updateAnswer3(it)
                }

            pageView.findViewById<EditText>(R.id.answer5)
                .bindAnswerTextWatcher(state.answer5) {
                    viewModel.updateAnswer5(it)
                }
        }

        updateNavButtons(state)
        updateIndicators(state)
    }

    private fun setupIndicators() {
        val indicatorContainer = binding.navPage.indicatorContainer
        indicatorContainer.removeAllViews()
        repeat(viewModel.uiState.value.totalPages) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(20, 20).apply {
                    setMargins(8, 0, 8, 0)
                }
                setBackgroundResource(R.drawable.ic_dot_circle_gray)
            }
            indicatorContainer.addView(dot)
        }
    }

    private fun updateIndicators(state: OppositeUiState) {
        val indicatorContainer = binding.navPage.indicatorContainer
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == state.currentPage) R.drawable.ic_dot_circle_black
                else R.drawable.ic_dot_circle_gray
            )
        }
    }

    private fun updateNavButtons(state: OppositeUiState) {
        binding.navPage.btnPrev.isEnabled = state.currentPage > 0
        binding.navPage.btnNext.text =
            if (state.currentPage == state.totalPages - 1) "완료 →" else "다음 →"
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