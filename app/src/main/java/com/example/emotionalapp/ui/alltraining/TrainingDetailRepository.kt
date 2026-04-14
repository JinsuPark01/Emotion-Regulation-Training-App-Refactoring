package com.example.emotionalapp.ui.alltraining

import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingMenuType
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.emotion.select.SelectReportActivity
import com.example.emotionalapp.ui.emotion.arc.ArcReportActivity
import com.example.emotionalapp.ui.emotion.anchor.AnchorReportActivity
import com.example.emotionalapp.ui.body.BodyTrainingReportActivity
import com.example.emotionalapp.ui.mind.AutoTrapReportActivity
import com.example.emotionalapp.ui.mind.trap.TrapReportActivity
import com.example.emotionalapp.ui.mind.art.ArtReportActivity
import com.example.emotionalapp.ui.mind.auto.AutoReportActivity
import com.example.emotionalapp.ui.expression.avoidance.AvoidanceReportActivity
import com.example.emotionalapp.ui.expression.stay.StayReportActivity
import com.example.emotionalapp.ui.expression.alternative.AlternativeReportActivity
import com.example.emotionalapp.ui.expression.opposite.OppositeReportActivity
import com.example.emotionalapp.ui.weekly.WeeklyReportActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class TrainingDetailRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getTrainingProgressData(): Result<TrainingProgressData> {
        val user = auth.currentUser
        val userEmail = user?.email

        if (userEmail == null) {
            return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
        }

        return suspendCancellableCoroutine { continuation ->
            db.collection("user").document(userEmail).get()
                .addOnSuccessListener { document ->
                    var userDiffDays = 0L
                    var countCompleteMap: Map<String, Long> = emptyMap()

                    if (document != null) {
                        if (document.contains("signupDate")) {
                            val timestamp = document.getTimestamp("signupDate")
                            if (timestamp != null) {
                                val koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul")
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).apply {
                                    timeZone = koreaTimeZone
                                }

                                val joinDateStr = dateFormat.format(timestamp.toDate())
                                val todayStr = dateFormat.format(Date())

                                val joinDate = dateFormat.parse(joinDateStr)
                                val todayDate = dateFormat.parse(todayStr)

                                if (joinDate != null && todayDate != null) {
                                    val diffMillis = todayDate.time - joinDate.time
                                    userDiffDays = TimeUnit.MILLISECONDS.toDays(diffMillis) + 1
                                }
                            }
                        }

                        if (document.contains("countComplete")) {
                            val rawMap = document["countComplete"] as? Map<*, *>
                            if (rawMap != null) {
                                countCompleteMap = rawMap.mapNotNull { (key, value) ->
                                    val k = key as? String
                                    val v = when (value) {
                                        is Long -> value
                                        is Number -> value.toLong()
                                        else -> null
                                    }
                                    if (k != null && v != null) k to v else null
                                }.toMap()
                            }
                        }
                    }

                    continuation.resume(
                        Result.success(
                            TrainingProgressData(
                                userDiffDays = userDiffDays,
                                countCompleteMap = countCompleteMap
                            )
                        )
                    )
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
        }
    }

    suspend fun getRecordItems(
        menuType: TrainingMenuType
    ): Result<List<DetailTrainingItem>> {
        val user = auth.currentUser
        val userEmail = user?.email

        if (userEmail == null) {
            return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
        }

        return try {
            val items = when (menuType) {
                TrainingMenuType.INTRO -> emptyList()
                TrainingMenuType.EMOTION -> loadEmotionRecordItems(userEmail)
                TrainingMenuType.BODY -> loadBodyRecordItems(userEmail)
                TrainingMenuType.MIND -> loadMindRecordItems(userEmail)
                TrainingMenuType.EXPRESSION -> loadExpressionRecordItems(userEmail)
            }

            Result.success(sortRecordItems(menuType, items))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun sortRecordItems(
        menuType: TrainingMenuType,
        items: List<DetailTrainingItem>
    ): List<DetailTrainingItem> {
        return when (menuType) {
            TrainingMenuType.MIND -> {
                val statsItems = items.filter { it.id == "mind_trap_statistics" }
                val normalItems = items.filterNot { it.id == "mind_trap_statistics" }
                    .sortedByDescending { parseTitleDateToMillis(it.title) }
                statsItems + normalItems
            }
            else -> items.sortedByDescending { parseTitleDateToMillis(it.title) }
        }
    }

    private suspend fun loadEmotionRecordItems(userEmail: String): List<DetailTrainingItem> =
        coroutineScope {
            val weeklyDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("weekly3")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "weekly_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "주간 점검 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EMOTION_TRAINING,
                            backgroundColorResId = R.color.button_color_emotion,
                            targetActivityClass = WeeklyReportActivity::class.java
                        )
                    }
            }

            val arcDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("emotionArc")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "emotion_arc_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "ARC 정서 경험 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EMOTION_TRAINING,
                            backgroundColorResId = R.color.button_color_emotion,
                            targetActivityClass = ArcReportActivity::class.java
                        )
                    }
            }

            val anchorDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("emotionAnchor")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "emotion_anchor_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "닻 내리기 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EMOTION_TRAINING,
                            backgroundColorResId = R.color.button_color_emotion,
                            targetActivityClass = AnchorReportActivity::class.java
                        )
                    }
            }

            val items = awaitAll(
                weeklyDeferred,
                arcDeferred,
                anchorDeferred
            ).flatten().toMutableList()

            items.add(
                0,
                DetailTrainingItem(
                    id = "emotion_select_record",
                    title = "감정 기록",
                    subtitle = "감정 기록 보기",
                    trainingType = TrainingType.EMOTION_TRAINING,
                    progressNumerator = "1",
                    progressDenominator = "1",
                    currentProgress = "보기",
                    backgroundColorResId = R.color.button_color_emotion,
                    targetActivityClass = SelectReportActivity::class.java,
                    reportDateMillis = null
                )
            )

            items
        }

    private suspend fun loadBodyRecordItems(userEmail: String): List<DetailTrainingItem> =
        coroutineScope {
            val bodyDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("bodyRecord")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = extractTimestamp(doc.get("date"))
                        val trainingId = doc.getString("trainingId").orEmpty()

                        val trainingName = when (trainingId) {
                            "bt_detail_002" -> "전체 몸 스캔 인식하기"
                            "bt_detail_003" -> "먹기 명상(음식의 오감 알아차리기)"
                            "bt_detail_004" -> "감정-신체 연결 인식"
                            "bt_detail_005" -> "특정 감각 집중하기"
                            "bt_detail_006" -> "바디 스캔(감각 알아차리기)"
                            "bt_detail_007" -> "바디 스캔(미세한 감각 변화 알아차리기)"
                            "bt_detail_008" -> "먹기 명상(감정과 신체 연결 알아차리기)"
                            else -> "훈련 소감"
                        }

                        createRecordItem(
                            id = "body_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = trainingName,
                            timestamp = ts,
                            trainingType = TrainingType.BODY_TRAINING,
                            backgroundColorResId = R.color.button_color_body,
                            targetActivityClass = BodyTrainingReportActivity::class.java
                        )
                    }
            }

            val weeklyDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("weekly3")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "weekly_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "주간 점검 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.BODY_TRAINING,
                            backgroundColorResId = R.color.button_color_body,
                            targetActivityClass = WeeklyReportActivity::class.java
                        )
                    }
            }

            awaitAll(bodyDeferred, weeklyDeferred).flatten()
        }

    private suspend fun loadMindRecordItems(userEmail: String): List<DetailTrainingItem> =
        coroutineScope {
            val weeklyDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("weekly3")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "weekly_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "주간 점검 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.MIND_WATCHING_TRAINING,
                            backgroundColorResId = R.color.button_color_mind,
                            targetActivityClass = WeeklyReportActivity::class.java
                        )
                    }
            }

            val artDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("mindArt")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "mind_art_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "인지적 평가 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.MIND_WATCHING_TRAINING,
                            backgroundColorResId = R.color.button_color_mind,
                            targetActivityClass = ArtReportActivity::class.java
                        )
                    }
            }

            val trapDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("mindTrap")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "mind_trap_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "생각의 덫 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.MIND_WATCHING_TRAINING,
                            backgroundColorResId = R.color.button_color_mind,
                            targetActivityClass = TrapReportActivity::class.java
                        )
                    }
            }

            val autoDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("mindAuto")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "mind_auto_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "자동적 평가 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.MIND_WATCHING_TRAINING,
                            backgroundColorResId = R.color.button_color_mind,
                            targetActivityClass = AutoReportActivity::class.java
                        )
                    }
            }

            val items = awaitAll(
                weeklyDeferred,
                artDeferred,
                trapDeferred,
                autoDeferred
            ).flatten().toMutableList()

            items.add(
                0,
                DetailTrainingItem(
                    id = "mind_trap_statistics",
                    title = "생각의 덫 기록",
                    subtitle = "생각의 덫 통계",
                    trainingType = TrainingType.MIND_WATCHING_TRAINING,
                    progressNumerator = "1",
                    progressDenominator = "1",
                    currentProgress = "보기",
                    backgroundColorResId = R.color.button_color_mind,
                    targetActivityClass = AutoTrapReportActivity::class.java,
                    reportDateMillis = null
                )
            )

            items
        }

    private suspend fun loadExpressionRecordItems(userEmail: String): List<DetailTrainingItem> =
        coroutineScope {
            val weeklyDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("weekly3")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "weekly_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "주간 점검 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                            backgroundColorResId = R.color.button_color_expression,
                            targetActivityClass = WeeklyReportActivity::class.java
                        )
                    }
            }

            val avoidanceDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("expressionAvoidance")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "expression_avoidance_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "회피 일지 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                            backgroundColorResId = R.color.button_color_expression,
                            targetActivityClass = AvoidanceReportActivity::class.java
                        )
                    }
            }

            val stayDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("expressionStay")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "expression_stay_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "정서 머무르기 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                            backgroundColorResId = R.color.button_color_expression,
                            targetActivityClass = StayReportActivity::class.java
                        )
                    }
            }

            val oppositeDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("expressionOpposite")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "expression_opposite_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "반대 행동하기 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                            backgroundColorResId = R.color.button_color_expression,
                            targetActivityClass = OppositeReportActivity::class.java
                        )
                    }
            }

            val alternativeDeferred = async {
                db.collection("user").document(userEmail)
                    .collection("expressionAlternative")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        val ts = doc.getTimestamp("date")
                        createRecordItem(
                            id = "expression_alternative_${doc.id}",
                            dateText = formatDate(ts),
                            trainingName = "대안 행동 찾기 기록 보기",
                            timestamp = ts,
                            trainingType = TrainingType.EXPRESSION_ACTION_TRAINING,
                            backgroundColorResId = R.color.button_color_expression,
                            targetActivityClass = AlternativeReportActivity::class.java
                        )
                    }
            }

            awaitAll(
                weeklyDeferred,
                avoidanceDeferred,
                stayDeferred,
                oppositeDeferred,
                alternativeDeferred
            ).flatten()
        }

    private fun createRecordItem(
        id: String,
        dateText: String,
        trainingName: String,
        timestamp: Timestamp?,
        trainingType: TrainingType,
        backgroundColorResId: Int,
        targetActivityClass: Class<out AppCompatActivity>?
    ): DetailTrainingItem? {
        if (timestamp == null) return null

        return DetailTrainingItem(
            id = id,
            title = dateText,
            subtitle = trainingName,
            trainingType = trainingType,
            progressNumerator = "1",
            progressDenominator = "1",
            currentProgress = "보기",
            backgroundColorResId = backgroundColorResId,
            targetActivityClass = targetActivityClass,
            reportDateMillis = timestamp.toDate().time
        )
    }

    private fun extractTimestamp(dateField: Any?): Timestamp? {
        return when (dateField) {
            is Timestamp -> dateField
            is Long -> Timestamp(Date(dateField))
            is String -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    Timestamp(sdf.parse(dateField)!!)
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
        }
    }

    private fun formatDate(ts: Timestamp?): String {
        return ts?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.toDate())
        } ?: "날짜 없음"
    }

    private fun parseTitleDateToMillis(dateText: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateText)?.time ?: Long.MIN_VALUE
        } catch (e: Exception) {
            Long.MIN_VALUE
        }
    }
}