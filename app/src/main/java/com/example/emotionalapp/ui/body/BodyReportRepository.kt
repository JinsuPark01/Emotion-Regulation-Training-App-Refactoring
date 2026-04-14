package com.example.emotionalapp.ui.body

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BodyReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun loadBodyReport(
        reportDateMillis: Long,
        trainingIdFromIntent: String,
        onSuccess: (BodyReportData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (reportDateMillis == -1L || trainingIdFromIntent.isBlank()) {
            onFailure("잘못된 접근입니다.")
            return
        }

        val user = auth.currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            onFailure("로그인이 필요합니다.")
            return
        }

        val reportDate = Date(reportDateMillis)

        val calendar = Calendar.getInstance().apply {
            time = reportDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = Timestamp(calendar.time)

        calendar.add(Calendar.DATE, 1)
        val startOfNextDay = Timestamp(calendar.time)

        db.collection("user")
            .document(userEmail)
            .collection("bodyRecord")
            .whereGreaterThanOrEqualTo("date", startOfDay)
            .whereLessThan("date", startOfNextDay)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull {
                    it.getString("trainingId") == trainingIdFromIntent
                }

                if (doc == null) {
                    onFailure("기록을 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                val trainingId = doc.getString("trainingId") ?: ""
                val content = doc.getString("content") ?: ""
                val date = doc.getTimestamp("date")?.toDate()

                val formattedDate = date?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }.format(it)
                } ?: ""

                onSuccess(
                    BodyReportData(
                        trainingId = trainingId,
                        trainingTitle = getTrainingNameById(trainingId),
                        reportDateText = formattedDate,
                        content = content
                    )
                )
            }
            .addOnFailureListener {
                onFailure("기록을 불러오지 못했습니다.")
            }
    }

    private fun getTrainingNameById(id: String): String {
        return when (id) {
            "bt_detail_002" -> "전체 몸 스캔 인식하기"
            "bt_detail_003" -> "먹기 명상 (음식의 오감 알아차리기)"
            "bt_detail_004" -> "감정-신체 연결 인식"
            "bt_detail_005" -> "특정 감각 집중하기"
            "bt_detail_006" -> "바디 스캔 (감각 알아차리기)"
            "bt_detail_007" -> "바디 스캔 (미세한 감각 변화 알아차리기)"
            "bt_detail_008" -> "먹기 명상 (감정과 신체 연결 알아차리기)"
            else -> "알 수 없는 훈련"
        }
    }
}