package com.example.emotionalapp.ui.mind

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.emotionalapp.R
import com.example.emotionalapp.util.setSingleListener
import kotlinx.coroutines.launch

class ArtActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: ArtViewModel by viewModels()

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
                    Toast.makeText(this@ArtActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    AlertDialog.Builder(this@ArtActivity)
                        .setTitle("훌륭합니다!")
                        .setMessage(
                            "⭐ 다른 시각에서 상황을 바라보는 연습은 처음엔 조금 어색할 수 있어요. " +
                                    "하지만 꾸준히 해보면 점차 익숙해져 나중에는 자연스럽게 여러 관점으로 생각할 수 있게 됩니다.\n\n" +
                                    "📝 이 연습에서 정해진 정답은 없다는 점을 기억하세요. " +
                                    "우리가 이렇게 다양한 시각을 연습하는 이유는 '더 맞는', ‘옳은’ 해석을 찾기 위해서가 아닙니다. " +
                                    "단지 우리가 처음에 떠올린 생각 외에도 다른 해석이 얼마든지 가능하다는 사실을 알아내기 위함입니다."
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
            btnNext.isEnabled = false

            val error = viewModel.validateCurrentPage()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                btnNext.isEnabled = true
                return@setSingleListener
            }

            if (viewModel.uiState.value.currentPage == 2) {
                viewModel.prepareSelectedImages()
            }

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

    private fun updatePage(state: ArtUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        val currentPage = state.currentPage
        titleText.text = if (currentPage == 0) "인지적 평가" else "모호한 그림 연습하기"

        val pageView = when (currentPage) {
            0 -> inflater.inflate(R.layout.fragment_mind_art_traning_0, pageContainer, false)
            1 -> inflater.inflate(R.layout.fragment_mind_art_traning_1, pageContainer, false)
            2 -> inflater.inflate(R.layout.fragment_mind_art_traning_2, pageContainer, false)
            in 3..8 -> inflater.inflate(R.layout.fragment_mind_art_traning_3, pageContainer, false)
            else -> inflater.inflate(R.layout.fragment_mind_art_traning_0, pageContainer, false)
        }

        pageContainer.addView(pageView)

        when (currentPage) {
            1 -> bindIntroImagePage(pageView)
            2 -> bindSelectImagesPage(pageView, state)
            in 3..8 -> bindQuestionPage(pageView, state)
        }
    }

    private fun bindIntroImagePage(pageView: View) {
        val image = pageView.findViewById<ImageView>(R.id.art0)
        image?.setImageResource(R.drawable.art0)
        image?.setOnClickListener {
            showZoomDialog(R.drawable.art0)
        }
    }

    private fun bindSelectImagesPage(pageView: View, state: ArtUiState) {
        val imageViews = listOf(
            pageView.findViewById<ImageView>(R.id.art1),
            pageView.findViewById<ImageView>(R.id.art2),
            pageView.findViewById<ImageView>(R.id.art3),
            pageView.findViewById<ImageView>(R.id.art4),
            pageView.findViewById<ImageView>(R.id.art5),
            pageView.findViewById<ImageView>(R.id.art6),
            pageView.findViewById<ImageView>(R.id.art7),
            pageView.findViewById<ImageView>(R.id.art8),
            pageView.findViewById<ImageView>(R.id.art9),
            pageView.findViewById<ImageView>(R.id.art10),
            pageView.findViewById<ImageView>(R.id.art11),
            pageView.findViewById<ImageView>(R.id.art12)
        )

        fun updateImageUI() {
            imageViews.forEachIndexed { index, imageView ->
                if (state.selectedImages.contains(index)) {
                    imageView?.scaleX = 0.9f
                    imageView?.scaleY = 0.9f
                } else {
                    imageView?.scaleX = 1.0f
                    imageView?.scaleY = 1.0f
                }
                imageView?.background = null
                imageView?.setPadding(0, 0, 0, 0)
            }
        }

        imageViews.forEachIndexed { index, imageView ->
            imageView?.setOnClickListener {
                val success = viewModel.toggleImageSelection(index)
                if (!success) {
                    Toast.makeText(this, "2개까지만 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        updateImageUI()
    }

    private fun bindQuestionPage(pageView: View, state: ArtUiState) {
        val question1 = pageView.findViewById<TextView>(R.id.question1)
        val question2 = pageView.findViewById<TextView>(R.id.question2)
        val answer1 = pageView.findViewById<EditText>(R.id.answer1)
        val answer2 = pageView.findViewById<EditText>(R.id.answer2)
        val imageView = pageView.findViewById<ImageView>(R.id.image)

        val currentPage = state.currentPage
        val isFirstImagePhase = currentPage in 3..5
        val imageIndex = if (isFirstImagePhase) 0 else 1
        val pageIndex = (currentPage - 3) % 3
        val startIndex = pageIndex * 2

        val imageResId = state.selectedImageResourceIds.getOrNull(imageIndex) ?: 0
        imageView?.setImageResource(imageResId)
        imageView?.visibility = View.VISIBLE

        val questions = listOf(
            "상황 1. 상상해보세요. 여러분의 마음이 지치고 불안한 날이라면\n예) 부모님과 다툰 후 / 친구와의 갈등 / 직장에서 큰 실수를 한 날\n\n1. 그림의 장면이 어떤 상황처럼 느껴지나요?",
            "1-1. 이 상황에서 어떤 감정이 느껴지나요?",
            "상황 2. 상상해보세요. 여러분의 기분이 좋은 날이라면\n예) 친구들과 즐거운 시간을 보낸 후 / 좋은 소식을 들은 날 / 따뜻한 날씨를 즐기는 날\n\n2. 같은 그림을 봤을 때, 어떤 상황으로 보이나요?",
            "2-1. 이 상황에서 느껴지는 감정은 무엇인가요?",
            "3. 같은 그림인데도 상황에 따라 다르게 느껴졌나요? 나의 감정 상태가 해석에 어떤 영향을 준 것 같나요?"
        )

        when (pageIndex) {
            0 -> {
                question1?.text = questions[0]
                question2?.text = questions[1]
                question2?.visibility = View.VISIBLE
                answer2?.visibility = View.VISIBLE
            }
            1 -> {
                question1?.text = questions[2]
                question2?.text = questions[3]
                question2?.visibility = View.VISIBLE
                answer2?.visibility = View.VISIBLE
            }
            2 -> {
                question1?.text = questions[4]
                question2?.visibility = View.GONE
                answer2?.visibility = View.GONE
            }
        }

        val savedAnswers = state.userAnswers[imageIndex]
        val savedAnswer1 = savedAnswers.getOrNull(startIndex).orEmpty()
        val savedAnswer2 = savedAnswers.getOrNull(startIndex + 1).orEmpty()

        answer1?.bindAnswerTextWatcher(
            initialText = savedAnswer1
        ) { newText ->
            if (newText != state.userAnswers[imageIndex].getOrNull(startIndex).orEmpty()) {
                viewModel.updateAnswer(
                    imageIndex = imageIndex,
                    answerIndex = startIndex,
                    text = newText
                )
            }
        }

        if (pageIndex != 2) {
            answer2?.visibility = View.VISIBLE
            answer2?.bindAnswerTextWatcher(
                initialText = savedAnswer2
            ) { newText ->
                if (newText != state.userAnswers[imageIndex].getOrNull(startIndex + 1).orEmpty()) {
                    viewModel.updateAnswer(
                        imageIndex = imageIndex,
                        answerIndex = startIndex + 1,
                        text = newText
                    )
                }
            }
        } else {
            answer2?.setText("")
            answer2?.visibility = View.GONE
        }

        imageView?.setOnClickListener {
            if (imageResId != 0) {
                showZoomDialog(imageResId)
            }
        }
    }

    private fun updateNavigationButtons(state: ArtUiState) {
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

    private fun showZoomDialog(imageResId: Int) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val view = layoutInflater.inflate(R.layout.dialog_zoom_image, null)
        val imageView = view.findViewById<ImageView>(R.id.zoomImage)

        Glide.with(this)
            .load(imageResId)
            .into(imageView)

        view.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(view)
        dialog.show()
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