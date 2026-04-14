package com.example.emotionalapp.ui.alltraining

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emotionalapp.R
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingMenuType
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.body.BodyExplanationActivity
import com.example.emotionalapp.ui.body.BodyIntroActivity
import com.example.emotionalapp.ui.emotion.anchor.AnchorActivity
import com.example.emotionalapp.ui.emotion.arc.ArcActivity
import com.example.emotionalapp.ui.emotion.select.SelectActivity
import com.example.emotionalapp.ui.expression.alternative.AlternativeActivity
import com.example.emotionalapp.ui.expression.avoidance.AvoidanceActivity
import com.example.emotionalapp.ui.expression.AvoidanceGuideActivity
import com.example.emotionalapp.ui.expression.DrivenActionGuideActivity
import com.example.emotionalapp.ui.expression.opposite.OppositeActivity
import com.example.emotionalapp.ui.expression.stay.StayActivity
import com.example.emotionalapp.ui.mind.art.ArtActivity
import com.example.emotionalapp.ui.mind.auto.AutoActivity
import com.example.emotionalapp.ui.mind.trap.TrapActivity
import com.example.emotionalapp.ui.weekly.WeeklyActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrainingDetailViewModel(
    private val repository: TrainingDetailRepository = TrainingDetailRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingDetailUiState(isLoading = true))
    val uiState: StateFlow<TrainingDetailUiState> = _uiState.asStateFlow()

    fun load(menuType: TrainingMenuType, menuTitle: String) {
        _uiState.value = TrainingDetailUiState(
            pageTitle = menuTitle,
            selectedTab = TrainingDetailTab.TRAINING,
            isLoading = true
        )

        viewModelScope.launch {
            val result = repository.getTrainingProgressData()

            result
                .onSuccess { progressData ->
                    _uiState.value = TrainingDetailUiState(
                        pageTitle = menuTitle,
                        selectedTab = TrainingDetailTab.TRAINING,
                        recordItems = emptyList(),
                        trainingItems = loadTrainingItems(
                            type = menuType,
                            userDiffDays = progressData.userDiffDays,
                            countCompleteMap = progressData.countCompleteMap
                        ),
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = TrainingDetailUiState(
                        pageTitle = menuTitle,
                        selectedTab = TrainingDetailTab.TRAINING,
                        recordItems = emptyList(),
                        trainingItems = emptyList(),
                        isLoading = false,
                        errorMessage = e.message ?: "데이터를 불러오지 못했습니다."
                    )
                }
        }
    }

    fun onTrainingTabClick() {
        _uiState.value = _uiState.value.copy(
            selectedTab = TrainingDetailTab.TRAINING
        )
    }

    fun onRecordTabClick(menuType: TrainingMenuType) {
        if (_uiState.value.recordItems.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                selectedTab = TrainingDetailTab.RECORD
            )
            return
        }

        viewModelScope.launch {
            val result = repository.getRecordItems(menuType)

            result
                .onSuccess { items ->
                    _uiState.value = _uiState.value.copy(
                        selectedTab = TrainingDetailTab.RECORD,
                        recordItems = items,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        selectedTab = TrainingDetailTab.RECORD,
                        errorMessage = e.message ?: "기록 데이터를 불러오지 못했습니다."
                    )
                }
        }
    }

    private fun getCurrentProgress(
        key: String,
        denominator: String,
        countCompleteMap: Map<String, Long>
    ): String {
        return if (denominator == "잠김") {
            "잠김"
        } else {
            val numerator = countCompleteMap[key]?.toString() ?: "0"
            "$numerator/$denominator"
        }
    }

    private fun getGoProgress(denominator: String): String {
        return if (denominator == "잠김") "잠김" else "GO"
    }

    private fun loadTrainingItems(
        type: TrainingMenuType,
        userDiffDays: Long,
        countCompleteMap: Map<String, Long>
    ): List<DetailTrainingItem> {
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
                val denominatorArr = arrayOf("99", "99", "99", "99")

                listOf(
                    DetailTrainingItem(
                        id = "weekly_training",
                        title = "주차별 점검",
                        subtitle = "질문지를 통한 마음 돌아보기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[0],
                        currentProgress = getCurrentProgress("weekly", denominatorArr[0], countCompleteMap),
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = WeeklyActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "emotion_detail_001",
                        title = "상태 기록하기",
                        subtitle = "정서와 관련된 신체 감각 찾기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = countCompleteMap["select"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[1],
                        currentProgress = getCurrentProgress("select", denominatorArr[1], countCompleteMap),
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = SelectActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "emotion_detail_002",
                        title = "현재에 닻 내리기",
                        subtitle = "특별한 경험을 기록하기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = countCompleteMap["anchor"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[2],
                        currentProgress = getCurrentProgress("anchor", denominatorArr[2], countCompleteMap),
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = AnchorActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "emotion_detail_003",
                        title = "ARC 정서 경험 기록",
                        subtitle = "특별한 경험을 기록하기",
                        trainingType = TrainingType.EMOTION_TRAINING,
                        progressNumerator = countCompleteMap["arc"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[3],
                        currentProgress = getCurrentProgress("arc", denominatorArr[3], countCompleteMap),
                        backgroundColorResId = R.color.button_color_emotion,
                        targetActivityClass = ArcActivity::class.java
                    )
                )
            }

            TrainingMenuType.BODY -> {
                val denominatorArr = arrayOf("99", "99", "99", "99", "99", "99", "99", "99")

                listOf(
                    DetailTrainingItem(
                        id = "weekly_training",
                        title = "주차별 점검",
                        subtitle = "질문지를 통한 마음 돌아보기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[0],
                        currentProgress = getCurrentProgress("weekly", denominatorArr[0], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = WeeklyActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_001",
                        title = "소개",
                        subtitle = "신체자각 훈련에 대한 설명",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = "1",
                        progressDenominator = "1",
                        currentProgress = getGoProgress("1"),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyIntroActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_002",
                        title = "전체 몸 스캔 인식하기",
                        subtitle = "정서와 관련된 신체 감각 찾기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_002"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[1],
                        currentProgress = getCurrentProgress("bt_detail_002", denominatorArr[1], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_003",
                        title = "먹기 명상",
                        subtitle = "음식의 오감 알아차리기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_003"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[2],
                        currentProgress = getCurrentProgress("bt_detail_003", denominatorArr[2], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_004",
                        title = "감정-신체 연결 인식",
                        subtitle = "특별한 경험을 기록하기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_004"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[3],
                        currentProgress = getCurrentProgress("bt_detail_004", denominatorArr[3], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_005",
                        title = "특정 감각 집중하기",
                        subtitle = "특별한 감각 집중하기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_005"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[4],
                        currentProgress = getCurrentProgress("bt_detail_005", denominatorArr[4], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_006",
                        title = "바디 스캔",
                        subtitle = "감각 알아차리기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_006"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[5],
                        currentProgress = getCurrentProgress("bt_detail_006", denominatorArr[5], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_007",
                        title = "바디 스캔",
                        subtitle = "미세한 감각 변화 알아차리기",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_007"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[6],
                        currentProgress = getCurrentProgress("bt_detail_007", denominatorArr[6], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "bt_detail_008",
                        title = "먹기 명상",
                        subtitle = "먹기명상을 통한 감정과 신체 연결 알아차림",
                        trainingType = TrainingType.BODY_TRAINING,
                        progressNumerator = countCompleteMap["bt_detail_008"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[7],
                        currentProgress = getCurrentProgress("bt_detail_008", denominatorArr[7], countCompleteMap),
                        backgroundColorResId = R.color.button_color_body,
                        targetActivityClass = BodyExplanationActivity::class.java
                    )
                )
            }

            TrainingMenuType.MIND -> {
                val denominatorArr = arrayOf("99", "99", "99", "99")

                listOf(
                    DetailTrainingItem(
                        id = "weekly_training",
                        title = "주차별 점검",
                        subtitle = "질문지를 통한 마음 돌아보기",
                        trainingType = TrainingType.MIND_WATCHING_TRAINING,
                        progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[0],
                        currentProgress = getCurrentProgress("weekly", denominatorArr[0], countCompleteMap),
                        backgroundColorResId = R.color.button_color_mind,
                        targetActivityClass = WeeklyActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "mind_detail_001",
                        title = "인지적 평가",
                        subtitle = "인지적 평가 교육 및 모호한 그림 해석을 진행합니다.",
                        trainingType = TrainingType.MIND_WATCHING_TRAINING,
                        progressNumerator = countCompleteMap["art"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[1],
                        currentProgress = getCurrentProgress("art", denominatorArr[1], countCompleteMap),
                        backgroundColorResId = R.color.button_color_mind,
                        targetActivityClass = ArtActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "mind_detail_002",
                        title = "생각의 덫",
                        subtitle = "생각의 덫을 파악하고 풀어내봅시다.",
                        trainingType = TrainingType.MIND_WATCHING_TRAINING,
                        progressNumerator = countCompleteMap["trap"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[2],
                        currentProgress = getCurrentProgress("trap", denominatorArr[2], countCompleteMap),
                        backgroundColorResId = R.color.button_color_mind,
                        targetActivityClass = TrapActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "mind_detail_003",
                        title = "자동적 평가",
                        subtitle = "3주차 훈련을 돌아보는 시간",
                        trainingType = TrainingType.MIND_WATCHING_TRAINING,
                        progressNumerator = countCompleteMap["auto"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[3],
                        currentProgress = getCurrentProgress("auto", denominatorArr[3], countCompleteMap),
                        backgroundColorResId = R.color.button_color_mind,
                        targetActivityClass = AutoActivity::class.java
                    )
                )
            }

            TrainingMenuType.EXPRESSION -> {
                val denominatorArr = arrayOf("99", "99", "99", "GO", "99", "99", "99")

                listOf(
                    DetailTrainingItem(
                        id = "weekly_training",
                        title = "주차별 점검",
                        subtitle = "질문지를 통한 마음 돌아보기",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = countCompleteMap["weekly"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[0],
                        currentProgress = getCurrentProgress("weekly", denominatorArr[0], countCompleteMap),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = WeeklyActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "avoidance_guide",
                        title = "정서회피 교육",
                        subtitle = "정서 회피에 대해 알아보기",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = "1",
                        progressDenominator = "1",
                        currentProgress = getGoProgress(denominatorArr[3]),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = AvoidanceGuideActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "avoidance_training",
                        title = "회피 일지 작성하기",
                        subtitle = "나의 회피 습관을 기록하고 관찰하기",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = countCompleteMap["avoidance"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[1],
                        currentProgress = getCurrentProgress("avoidance", denominatorArr[1], countCompleteMap),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = AvoidanceActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "stay_training",
                        title = "정서 머무르기",
                        subtitle = "감정을 피하지 않고 느껴보는 연습",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = countCompleteMap["stay"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[2],
                        currentProgress = getCurrentProgress("stay", denominatorArr[2], countCompleteMap),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = StayActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "driven_action_guide",
                        title = "정서-주도 행동 교육",
                        subtitle = "정서-주도 행동에 대해 알아보기",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = "1",
                        progressDenominator = "1",
                        currentProgress = getGoProgress(denominatorArr[3]),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = DrivenActionGuideActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "opposite_training",
                        title = "반대 행동 하기",
                        subtitle = "감정과 반대로 행동하는 연습",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = countCompleteMap["opposite"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[4],
                        currentProgress = getCurrentProgress("opposite", denominatorArr[4], countCompleteMap),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = OppositeActivity::class.java
                    ),
                    DetailTrainingItem(
                        id = "alternative_training",
                        title = "대안 행동 찾기",
                        subtitle = "감정을 다루는 다른 방법 찾기",
                        trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                        progressNumerator = countCompleteMap["alternative"]?.toString() ?: "0",
                        progressDenominator = denominatorArr[5],
                        currentProgress = getCurrentProgress("alternative", denominatorArr[5], countCompleteMap),
                        backgroundColorResId = R.color.button_color_expression,
                        targetActivityClass = AlternativeActivity::class.java
                    )
                )
            }
        }
    }
}