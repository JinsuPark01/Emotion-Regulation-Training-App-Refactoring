package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.emotionalapp.R
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingMenuItem
import com.example.emotionalapp.data.TrainingMenuType
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.emotion.AnchorActivity
import com.example.emotionalapp.ui.emotion.ArcActivity
import com.example.emotionalapp.ui.emotion.EmotionReportActivity
import com.example.emotionalapp.ui.emotion.SelectActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity

class TrainingDetailFragment : Fragment() {

    companion object {
        private const val ARG_MENU_ID = "arg_menu_id"
        private const val ARG_MENU_TITLE = "arg_menu_title"
        private const val ARG_MENU_TYPE = "arg_menu_type"

        fun newInstance(item: TrainingMenuItem): TrainingDetailFragment {
            return TrainingDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MENU_ID, item.id)
                    putString(ARG_MENU_TITLE, item.title)
                    putString(ARG_MENU_TYPE, item.type.name)
                }
            }
        }
    }

    private val menuId: String by lazy {
        arguments?.getString(ARG_MENU_ID).orEmpty()
    }

    private val menuTitle: String by lazy {
        arguments?.getString(ARG_MENU_TITLE).orEmpty()
    }

    private val menuType: TrainingMenuType by lazy {
        TrainingMenuType.valueOf(arguments?.getString(ARG_MENU_TYPE).orEmpty())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val recordItems = loadRecordItems(menuType)
                val trainingItems = loadTrainingItems(menuType)

                TrainingDetailScreen(
                    pageTitle = menuTitle,
                    recordItems = recordItems,
                    trainingItems = trainingItems,
                    onBackClick = {
                        parentFragmentManager.popBackStack()
                    },
                    onRecordTabClick = {
                        // 필요하면 로그/추가 처리
                    },
                    onTrainingTabClick = {
                        // 필요하면 로그/추가 처리
                    },
                    onDetailItemClick = { clickedItem ->
                        if (clickedItem.currentProgress == "잠김") {
                            Toast.makeText(requireContext(), "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                            return@TrainingDetailScreen
                        }

                        if (clickedItem.progressDenominator == clickedItem.progressNumerator) {
                            Toast.makeText(requireContext(), "모두 완료한 훈련입니다.", Toast.LENGTH_SHORT).show()
                            return@TrainingDetailScreen
                        }

                        clickedItem.targetActivityClass?.let { targetClass ->
                            val intent = Intent(requireContext(), targetClass).apply {
                                putExtra("TRAINING_ID", clickedItem.id)
                                putExtra("TRAINING_TITLE", clickedItem.title)
                            }
                            startActivity(intent)
                        } ?: run {
                            Toast.makeText(
                                requireContext(),
                                "${clickedItem.title}: 상세 페이지 준비 중입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

    private fun loadRecordItems(type: TrainingMenuType): List<DetailTrainingItem> {
        return when (type) {
            TrainingMenuType.INTRO -> {
                listOf(
                    DetailTrainingItem(
                        id = "intro_record_001",
                        title = "INTRO 기록 보기",
                        subtitle = "INTRO 기록 예시",
                        trainingType = TrainingType.INTRO,
                        progressNumerator = "1",
                        progressDenominator = "1",
                        currentProgress = "1/1",
                        backgroundColorResId = R.color.button_color_intro,
                        targetActivityClass = null
                    )
                )
            }

            TrainingMenuType.EMOTION -> {
                listOf(
                    DetailTrainingItem(
                        id = "emotion_report",
                        title = "정서 기록 보기",
                        subtitle = "정서 기록 확인하기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = "1",
                        progressDenominator = "4",
                        currentProgress = "1/4",
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = EmotionReportActivity::class.java
                    )
                )
            }

            TrainingMenuType.BODY -> {
                listOf(
                    DetailTrainingItem(
                        id = "body_record_001",
                        title = "신체 기록 보기",
                        subtitle = "신체 자각 기록 확인",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "4",
                        currentProgress = "0/4",
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = null
                    )
                )
            }

            TrainingMenuType.MIND -> {
                listOf(
                    DetailTrainingItem(
                        id = "mind_record_001",
                        title = "인지 기록 보기",
                        subtitle = "인지 재구성 기록 확인",
                        trainingType = TrainingType.MIND_WATCHING_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "4",
                        currentProgress = "0/4",
                        backgroundColorResId = R.color.button_color_mind,
                        targetActivityClass = null
                    )
                )
            }

            TrainingMenuType.EXPRESSION -> {
                listOf(
                    DetailTrainingItem(
                        id = "expression_record_001",
                        title = "행동 기록 보기",
                        subtitle = "정서 표현 기록 확인",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "4",
                        currentProgress = "0/4",
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = null
                    )
                )
            }
        }
    }

    private fun loadTrainingItems(type: TrainingMenuType): List<DetailTrainingItem> {
        return when (type) {
            TrainingMenuType.INTRO -> {
                listOf(
                    DetailTrainingItem(
                        id = "intro_training_001",
                        title = "INTRO 시작하기",
                        subtitle = "감정의 세계로 떠나는 첫 걸음",
                        trainingType = TrainingType.INTRO,
                        progressNumerator = "0",
                        progressDenominator = "1",
                        currentProgress = "0/1",
                        backgroundColorResId = R.color.button_color_intro,
                        targetActivityClass = null
                    )
                )
            }

            TrainingMenuType.EMOTION -> {
                listOf(
                    DetailTrainingItem(
                        id = "weekly_training",
                        title = "주차별 점검",
                        subtitle = "질문지를 통한 마음 돌아보기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = WeeklyActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "emotion_detail_001",
                        title = "상태 기록하기",
                        subtitle = "정서와 관련된 신체 감각 찾기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = SelectActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "emotion_detail_002",
                        title = "현재에 닻 내리기",
                        subtitle = "특별한 경험을 기록하기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = AnchorActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "emotion_detail_003",
                        title = "ARC 정서 경험 기록",
                        subtitle = "특별한 경험을 기록하기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = ArcActivity::class.java
                    )
                )
            }

            TrainingMenuType.BODY -> {
                listOf(
                    DetailTrainingItem(
                        id = "body_training_001",
                        title = "신체자각 훈련 1",
                        subtitle = "몸의 반응을 관찰하기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = null
                    )
                )
            }

            TrainingMenuType.MIND -> {
                listOf(
                    DetailTrainingItem(
                        id = "mind_training_001",
                        title = "인지재구성 훈련 1",
                        subtitle = "생각의 틀을 살펴보기",
                        trainingType = TrainingType.MIND_WATCHING_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_mind,
                        targetActivityClass = null
                    )
                )
            }

            TrainingMenuType.EXPRESSION -> {
                listOf(
                    DetailTrainingItem(
                        id = "expression_training_001",
                        title = "정서표현 훈련 1",
                        subtitle = "건강하게 감정을 표현하기",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = "0",
                        progressDenominator = "99",
                        currentProgress = "0/99",
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = null
                    )
                )
            }
        }
    }
}