package com.example.emotionalapp.ui.expression.stay

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.util.setSingleListener
import com.google.android.material.progressindicator.CircularProgressIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class StayActivity : AppCompatActivity() {

    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var indicatorContainer: LinearLayout
    private lateinit var pageContainer: FrameLayout
    private lateinit var titleText: TextView

    private val viewModel: StayViewModel by viewModels()

    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var selectedEmotionView: View? = null
    private var hasShownRandomMessage = false

    private val emotionIcons = mapOf(
        "행복" to R.drawable.emotion_happy,
        "즐거움" to R.drawable.emotion_joy,
        "자신감" to R.drawable.emotion_confident,
        "슬픔" to R.drawable.emotion_sad,
        "두려움" to R.drawable.emotion_fear,
        "당황" to R.drawable.emotion_embarrassed,
        "걱정" to R.drawable.emotion_anxious,
        "짜증" to R.drawable.emotion_annoyed,
        "분노" to R.drawable.emotion_angry
    )

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

        setupIndicators()
        observeUiState()
        setupListeners()
        viewModel.loadInitialState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updatePage(state)

                state.errorMessage?.let {
                    Toast.makeText(this@StayActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@StayActivity, "정서 머무르기 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupListeners() {
        btnPrev.setOnClickListener {
            if (viewModel.uiState.value.currentPage == 1) {
                stopTimerAndMusic()
            }
            viewModel.goPrevPage()
        }

        btnNext.setSingleListener {
            val error = viewModel.validateCurrentPage()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                return@setSingleListener
            }

            when (viewModel.uiState.value.currentPage) {
                1 -> {
                    stopTimerAndMusic()
                    viewModel.goNextPage()
                }

                3 -> {
                    btnNext.isEnabled = false
                    viewModel.saveTraining()
                    btnNext.isEnabled = true
                }

                else -> {
                    viewModel.goNextPage()
                }
            }
        }
    }

    private fun stopTimerAndMusic() {
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun setupIndicators() {
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

    private fun updatePage(state: StayUiState) {
        val inflater = LayoutInflater.from(this)
        pageContainer.removeAllViews()

        titleText.text = when (state.currentPage) {
            0, 1 -> "정서 머무르기"
            2 -> "감정 기록하기"
            3 -> "마무리"
            else -> ""
        }

        val layoutRes = when (state.currentPage) {
            0 -> R.layout.fragment_expression_stay_training_0
            1 -> R.layout.fragment_expression_stay_training_1
            2 -> R.layout.fragment_expression_stay_training_2
            3 -> R.layout.fragment_expression_stay_training_3
            else -> R.layout.fragment_expression_stay_training_0
        }

        val view = inflater.inflate(layoutRes, pageContainer, false)
        pageContainer.addView(view)

        when (state.currentPage) {
            0 -> setupPage0(view, state)
            1 -> setupPage1(view, state)
            2 -> setupPage2(view, state)
            3 -> {}
        }

        updateNavigationButtons(state)
    }

    private fun updateNavigationButtons(state: StayUiState) {
        btnPrev.isEnabled = state.currentPage != 0 && state.currentPage != 2
        btnPrev.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (btnPrev.isEnabled) "#00897B" else "#D9D9D9")
        )

        btnNext.text = if (state.currentPage == state.totalPages - 1) "완료 →" else "다음 →"

        for (i in 0 until indicatorContainer.childCount) {
            indicatorContainer.getChildAt(i).setBackgroundResource(
                if (i == state.currentPage) R.drawable.ic_dot_circle_black
                else R.drawable.ic_dot_circle_gray
            )
        }
    }

    private fun setupPage0(view: View, state: StayUiState) {
        val gridEmotions = view.findViewById<GridLayout>(R.id.grid_emotions)
        gridEmotions.removeAllViews()
        selectedEmotionView = null

        viewModel.emotions.forEach { emotion ->
            val iconResId = emotionIcons[emotion] ?: return@forEach
            val emotionView = createEmotionView(emotion, iconResId, gridEmotions, state)
            gridEmotions.addView(emotionView)
        }

        val rgTimer = view.findViewById<RadioGroup>(R.id.rg_timer_duration)
        val rb1 = view.findViewById<RadioButton>(R.id.rb_1_min)
        val rb2 = view.findViewById<RadioButton>(R.id.rb_2_min)
        val rb3 = view.findViewById<RadioButton>(R.id.rb_3_min)

        if (state.isFirstTraining) {
            rb1.isEnabled = true
            rb2.isEnabled = false
            rb3.isEnabled = false
            rgTimer.check(R.id.rb_1_min)
        } else {
            rb1.isEnabled = true
            rb2.isEnabled = true
            rb3.isEnabled = true

            when (state.selectedTimerMillis) {
                60_000L -> rgTimer.check(R.id.rb_1_min)
                180_000L -> rgTimer.check(R.id.rb_3_min)
                else -> rgTimer.check(R.id.rb_2_min)
            }
        }

        rgTimer.setOnCheckedChangeListener { _, checkedId ->
            val millis = when (checkedId) {
                R.id.rb_1_min -> 60_000L
                R.id.rb_3_min -> 180_000L
                else -> 120_000L
            }
            viewModel.selectTimerMillis(millis)
        }
    }

    private fun createEmotionView(
        emotion: String,
        iconResId: Int,
        parent: GridLayout,
        state: StayUiState
    ): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_emotion_card, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.iv_emotion)
        val textView = view.findViewById<TextView>(R.id.tv_emotion)

        imageView.setImageResource(iconResId)
        textView.text = emotion

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(8, 8, 8, 8)
        }
        view.layoutParams = params

        if (state.selectedEmotion == emotion) {
            view.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
                setTint(ContextCompat.getColor(this@StayActivity, R.color.purple_500))
            }
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            selectedEmotionView = view
        }

        view.setOnClickListener {
            selectedEmotionView?.let { oldView ->
                oldView.background = ContextCompat.getDrawable(this, R.drawable.bg_topic_button)
                oldView.findViewById<TextView>(R.id.tv_emotion)
                    .setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }

            it.background = ContextCompat.getDrawable(this, R.drawable.bg_round_green_button)?.apply {
                setTint(ContextCompat.getColor(this@StayActivity, R.color.purple_500))
            }
            (it as LinearLayout).findViewById<TextView>(R.id.tv_emotion)
                .setTextColor(ContextCompat.getColor(this, android.R.color.white))

            selectedEmotionView = it
            viewModel.selectEmotion(emotion)
        }

        return view
    }

    private fun setupPage1(view: View, state: StayUiState) {
        val progressCircular = view.findViewById<CircularProgressIndicator>(R.id.progress_circular)
        val tvTimer = view.findViewById<TextView>(R.id.tv_timer)
        val tvGuidance = view.findViewById<TextView>(R.id.tv_guidance)
        val btnVolumeToggle = view.findViewById<ImageView>(R.id.btn_volume_toggle)

        startTimer(progressCircular, tvTimer, tvGuidance, state)
        setupMusic(state)
        updateVolumeIcon(btnVolumeToggle, state.isMuted)

        btnVolumeToggle.setOnClickListener {
            viewModel.toggleMute()
            val nowMuted = !state.isMuted
            if (nowMuted) {
                mediaPlayer?.setVolume(0f, 0f)
            } else {
                mediaPlayer?.setVolume(1f, 1f)
                mediaPlayer?.start()
            }
            updateVolumeIcon(btnVolumeToggle, nowMuted)
        }
    }

    private fun startTimer(
        progressCircular: CircularProgressIndicator,
        tvTimer: TextView,
        tvGuidance: TextView,
        state: StayUiState
    ) {
        val totalSeconds = (state.selectedTimerMillis / 1000).toInt()
        progressCircular.max = totalSeconds
        progressCircular.progress = totalSeconds

        hasShownRandomMessage = false
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(state.selectedTimerMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
                progressCircular.progress = secondsRemaining.toInt()
                updateGuidanceText(tvGuidance, secondsRemaining, state)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                progressCircular.progress = 0
                updateGuidanceText(tvGuidance, 0L, state)
            }
        }.start()
    }

    private fun updateGuidanceText(
        tvGuidance: TextView,
        secondsRemaining: Long,
        state: StayUiState
    ) {
        val totalSeconds = (state.selectedTimerMillis / 1000).toInt()
        val positiveEmotions = listOf("행복", "즐거움", "자신감")
        val isPositive = state.selectedEmotion?.let { it in positiveEmotions } ?: false

        val currentText = tvGuidance.text.toString()
        val guidanceText = when {
            secondsRemaining == 0L -> "수고하셨어요.\n감정을 없애려 하지 않고 잠시 바라본 것만으로도 충분합니다."
            secondsRemaining == totalSeconds.toLong() -> "그 감정을 억누르지 말고, 지금 이 순간 그대로 느껴보세요."
            secondsRemaining == totalSeconds - 30L -> if (isPositive) {
                "이 순간의 따뜻함을 온전히 느껴보세요."
            } else {
                "이 감정을 느껴도 괜찮아요."
            }

            secondsRemaining == totalSeconds - 60L -> {
                "감정을 더 느껴보고 싶다면 계속 머물러도 좋고,\n힘들다면 여기서 마무리해도 괜찮아요."
            }

            secondsRemaining <= totalSeconds - 90L && !hasShownRandomMessage -> {
                hasShownRandomMessage = true
                listOf(
                    "지금 느껴지는 감정에 집중해볼까요?",
                    "그 감정은 어디에서 시작되었나요?",
                    "지금 이 감정이 몸의 어떤 부위에서 느껴지는지 살펴볼까요?",
                    "숨은 천천히 쉬면서, 감정을 그냥 거기에 두세요."
                ).random()
            }

            else -> currentText
        }

        if (tvGuidance.text != guidanceText) {
            tvGuidance.text = guidanceText
        }
    }

    private fun setupMusic(state: StayUiState) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.meditation_music)
        mediaPlayer?.isLooping = true

        if (state.isMuted) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(1f, 1f)
            mediaPlayer?.start()
        }
    }

    private fun updateVolumeIcon(btnVolumeToggle: ImageView, isMuted: Boolean) {
        if (isMuted) {
            btnVolumeToggle.setImageResource(R.drawable.ic_volume_off)
        } else {
            btnVolumeToggle.setImageResource(R.drawable.ic_volume_on)
        }
    }

    private fun setupPage2(view: View, state: StayUiState) {
        val etClarifyEmotion = view.findViewById<EditText>(R.id.edit_text_emotion_clarified)
        val etMoodChange = view.findViewById<EditText>(R.id.edit_text_mood_changed)

        etClarifyEmotion.bindAnswerTextWatcher(state.clarifiedEmotion) {
            viewModel.updateClarifiedEmotion(it)
        }

        etMoodChange.bindAnswerTextWatcher(state.moodChanged) {
            viewModel.updateMoodChanged(it)
        }
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.uiState.value.isMuted) {
            mediaPlayer?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
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