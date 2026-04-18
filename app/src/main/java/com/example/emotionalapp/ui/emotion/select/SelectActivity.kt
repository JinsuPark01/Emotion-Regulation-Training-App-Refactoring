package com.example.emotionalapp.ui.emotion.select

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.emotion.select.SelectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class SelectActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnSelect: TextView

    private lateinit var mindButtons: List<LinearLayout>
    private lateinit var bodyButtons: List<LinearLayout>

    private lateinit var accordionWhatIs: LinearLayout
    private lateinit var tvWhatIsDesc: TextView
    private lateinit var iconArrow: ImageView

    private lateinit var accordionHowTo: LinearLayout
    private lateinit var layoutHowToDesc: LinearLayout
    private lateinit var iconArrowHowTo: ImageView

    private lateinit var accordionCaution: LinearLayout
    private lateinit var layoutCautionDesc: LinearLayout
    private lateinit var iconArrowCaution: ImageView

    private val viewModel: SelectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotion_select)

        bindViews()
        setupStaticListeners()
        setupFeelingButtons()
        setupAccordionViews()
        observeUiState()

        viewModel.loadInitialState()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        btnSelect = findViewById(R.id.btnSelect)

        mindButtons = listOf(
            findViewById(R.id.btnMind1),
            findViewById(R.id.btnMind2),
            findViewById(R.id.btnMind3),
            findViewById(R.id.btnMind4),
            findViewById(R.id.btnMind5),
        )

        bodyButtons = listOf(
            findViewById(R.id.btnBody1),
            findViewById(R.id.btnBody2),
            findViewById(R.id.btnBody3),
            findViewById(R.id.btnBody4),
            findViewById(R.id.btnBody5),
        )

        accordionWhatIs = findViewById(R.id.accordionWhatIS)
        tvWhatIsDesc = findViewById(R.id.tvWhatIsDesc)
        iconArrow = findViewById(R.id.iconArrow)

        accordionHowTo = findViewById(R.id.accordionHowTo)
        layoutHowToDesc = findViewById(R.id.layoutHowToDesc)
        iconArrowHowTo = findViewById(R.id.iconArrowHowTo)

        accordionCaution = findViewById(R.id.accordionCaution)
        layoutCautionDesc = findViewById(R.id.layoutCautionDesc)
        iconArrowCaution = findViewById(R.id.iconArrowCaution)
    }

    private fun setupStaticListeners() {
        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        btnSelect.setOnClickListener {
            viewModel.saveSelection()
        }
    }

    private fun setupFeelingButtons() {
        mindButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.selectMind(index)
            }
        }

        bodyButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.selectBody(index)
            }
        }
    }

    private fun setupAccordionViews() {
        accordionWhatIs.setOnClickListener {
            viewModel.toggleWhatIs()
        }

        accordionHowTo.setOnClickListener {
            viewModel.toggleHowTo()
        }

        accordionCaution.setOnClickListener {
            viewModel.toggleCaution()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateFeelingButtons(mindButtons, state.selectedMind)
                updateFeelingButtons(bodyButtons, state.selectedBody)

                tvWhatIsDesc.visibility = if (state.isWhatIsExpanded) View.VISIBLE else View.GONE
                layoutHowToDesc.visibility = if (state.isHowToExpanded) View.VISIBLE else View.GONE
                layoutCautionDesc.visibility = if (state.isCautionExpanded) View.VISIBLE else View.GONE

                btnSelect.isEnabled = state.isSelectButtonEnabled && !state.isSaving
                btnSelect.text = state.selectButtonText
                btnSelect.backgroundTintList = if (btnSelect.isEnabled) {
                    ColorStateList.valueOf(Color.parseColor("#00897B"))
                } else {
                    ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
                }

                state.errorMessage?.let {
                    Toast.makeText(this@SelectActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@SelectActivity, "상태 기록이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun updateFeelingButtons(buttons: List<LinearLayout>, selectedIndex: Int) {
        buttons.forEachIndexed { index, btn ->
            btn.alpha = if (index == selectedIndex) 1.0f else 0.3f
        }
    }
}