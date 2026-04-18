package com.example.emotionalapp.ui.expression.alternative

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionalapp.R
import com.example.emotionalapp.adapter.AlternativeActionAdapter
import com.example.emotionalapp.adapter.DetailedEmotionAdapter
import com.example.emotionalapp.data.AlternativeActionItem
import com.example.emotionalapp.databinding.ActivityAlternativeActionBinding
import com.example.emotionalapp.util.setSingleListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class AlternativeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlternativeActionBinding
    private val viewModel: AlternativeViewModel by viewModels()
    private var selectedEmotionButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlternativeActionBinding.inflate(layoutInflater)
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
                    Toast.makeText(this@AlternativeActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@AlternativeActivity, "훈련 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
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

    private fun updatePage(state: AlternativeUiState) {
        val inflater = LayoutInflater.from(this)
        binding.pageContainer.removeAllViews()
        selectedEmotionButton = null

        val pageView = when (state.currentPage) {
            0 -> inflater.inflate(R.layout.page_alternative_action_0_guide, binding.pageContainer, false)
            1 -> inflater.inflate(R.layout.page_alternative_action_1_emotion, binding.pageContainer, false)
            2 -> inflater.inflate(R.layout.page_alternative_action_2_suggestion, binding.pageContainer, false)
            3 -> inflater.inflate(R.layout.page_alternative_action_3_action, binding.pageContainer, false)
            4 -> inflater.inflate(R.layout.page_alternative_action_4, binding.pageContainer, false)
            else -> throw IllegalStateException("Invalid page")
        }

        binding.pageContainer.addView(pageView)

        when (state.currentPage) {
            1 -> bindPage1(pageView, state)
            2 -> bindPage2(pageView, state)
            3 -> bindPage3(pageView, state)
        }

        updateNavButtons(state)
        updateIndicators(state)
    }

    private fun bindPage1(view: View, state: AlternativeUiState) {
        view.findViewById<EditText>(R.id.edit_situation)
            .bindAnswerTextWatcher(state.situation) {
                viewModel.updateSituation(it)
            }

        view.findViewById<EditText>(R.id.tv_direct_input_hint)
            .bindAnswerTextWatcher(state.customEmotion) {
                viewModel.updateCustomEmotion(it)
            }

        setupEmotionButtons(view, state)
    }

    private fun bindPage2(view: View, state: AlternativeUiState) {
        val alternativeQ = view.findViewById<TextView>(R.id.alternativeQ)

        if (state.selectedEmotion != "직접 입력") {
            alternativeQ.visibility = View.VISIBLE
            setupSuggestionPage(view, state)
        } else {
            alternativeQ.visibility = View.GONE
        }

        view.findViewById<EditText>(R.id.edit_custom_action)
            .bindAnswerTextWatcher(state.customAlternative) {
                viewModel.updateCustomAlternative(it)
            }
    }

    private fun bindPage3(view: View, state: AlternativeUiState) {
        view.findViewById<EditText>(R.id.edit_action_taken)
            .bindAnswerTextWatcher(state.finalActionTaken) {
                viewModel.updateFinalActionTaken(it)
            }
    }

    private fun setupEmotionButtons(view: View, state: AlternativeUiState) {
        val gridEmotions = view.findViewById<GridLayout>(R.id.grid_emotions)
        gridEmotions.removeAllViews()

        viewModel.emotions.forEach { emotion ->
            val button = createEmotionButton(emotion)
            gridEmotions.addView(button)

            if (emotion == state.selectedEmotion) {
                highlightSelectedEmotion(button)
            }

            button.setOnClickListener {
                viewModel.selectEmotion(emotion)
            }
        }

        val tvDirectInputHint = view.findViewById<TextView>(R.id.tv_direct_input_hint)
        val detailedContainer = view.findViewById<LinearLayout>(R.id.detailed_emotion_container)

        if (state.selectedEmotion == "직접 입력") {
            detailedContainer.visibility = View.GONE
            tvDirectInputHint.visibility = View.VISIBLE
        } else {
            detailedContainer.visibility = View.VISIBLE
            tvDirectInputHint.visibility = View.GONE

            val detailedResId = viewModel.detailedEmotionResMap[state.selectedEmotion]
            if (detailedResId != null) {
                val detailedEmotions = resources.getStringArray(detailedResId).toList()
                val recyclerDetailed = view.findViewById<RecyclerView>(R.id.recycler_detailed_emotions)
                recyclerDetailed.layoutManager = LinearLayoutManager(this)
                recyclerDetailed.adapter = DetailedEmotionAdapter(
                    detailedEmotions,
                    state.selectedDetailedEmotionPosition
                ) { position, item ->
                    viewModel.selectDetailedEmotion(position, item)
                }
            }
        }
    }

    private fun setupSuggestionPage(view: View, state: AlternativeUiState) {
        val alternativeResId = viewModel.alternativeActionResMap[state.selectedEmotion]
        if (alternativeResId != null) {
            val alternativeActions = resources.getStringArray(alternativeResId)
                .toList()
                .map { AlternativeActionItem(it) }

            val recyclerAlternative = view.findViewById<RecyclerView>(R.id.recycler_alternative_actions)
            recyclerAlternative.layoutManager = LinearLayoutManager(this)
            recyclerAlternative.adapter = AlternativeActionAdapter(
                alternativeActions,
                state.selectedAlternativePosition
            ) { position, item ->
                viewModel.selectAlternative(position, item.actionText)
            }
        }
    }

    private fun highlightSelectedEmotion(button: Button) {
        selectedEmotionButton?.apply {
            background = ContextCompat.getDrawable(this@AlternativeActivity, R.drawable.bg_topic_button)
            setTextColor(ContextCompat.getColor(this@AlternativeActivity, android.R.color.black))
        }

        button.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
            setTint(ContextCompat.getColor(this@AlternativeActivity, R.color.purple_500))
        }
        button.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        selectedEmotionButton = button
    }

    private fun createEmotionButton(text: String): Button {
        return Button(this).apply {
            this.text = text
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            background = ContextCompat.getDrawable(this@AlternativeActivity, R.drawable.bg_topic_button)
            gravity = Gravity.CENTER
            setPadding(16, 24, 16, 24)
            isAllCaps = false
        }
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

    private fun updateIndicators(state: AlternativeUiState) {
        val indicatorContainer = binding.navPage.indicatorContainer
        for (i in 0 until indicatorContainer.childCount) {
            val dot = indicatorContainer.getChildAt(i)
            dot.setBackgroundResource(
                if (i == state.currentPage) R.drawable.ic_dot_circle_black
                else R.drawable.ic_dot_circle_gray
            )
        }
    }

    private fun updateNavButtons(state: AlternativeUiState) {
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