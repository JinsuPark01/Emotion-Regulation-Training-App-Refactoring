package com.example.emotionalapp.ui.emotion.anchor

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class AnchorReportRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun loadAnchorReport(
        reportDateMillis: Long,
        onSuccess: (AnchorReportData) -> Unit,
        onFailure: (String) -> Unit,
        onRequireLogin: () -> Unit
    ) {
        if (reportDateMillis == -1L) {
            onFailure("잘못된 보고서 정보입니다.")
            return
        }

        val user = auth.currentUser
        val userEmail = user?.email

        if (user == null || userEmail == null) {
            onRequireLogin()
            return
        }

        val reportTimestamp = Timestamp(Date(reportDateMillis))

        db.collection("user")
            .document(userEmail)
            .collection("emotionAnchor")
            .whereEqualTo("date", reportTimestamp)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()

                if (doc == null) {
                    onFailure("해당 기록을 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                val timestamp = doc.getTimestamp("date")
                if (timestamp == null) {
                    onFailure("기록 날짜 정보가 없습니다.")
                    return@addOnSuccessListener
                }

                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }.format(timestamp.toDate())

                val selectedCue = doc.getString("selectedCue") ?: ""

                val elements = doc.get("elements") as? Map<*, *>
                val thought = elements?.get("thought") as? String ?: ""
                val sensation = elements?.get("sensation") as? String ?: ""
                val behavior = elements?.get("behavior") as? String ?: ""

                val evaluation = doc.get("evaluation") as? Map<*, *>
                val change = evaluation?.get("change") as? String ?: ""
                val effect = evaluation?.get("effect") as? String ?: ""

                onSuccess(
                    AnchorReportData(
                        dateText = dateString,
                        selectedCue = selectedCue,
                        thought = thought,
                        sensation = sensation,
                        behavior = behavior,
                        change = change,
                        effect = effect
                    )
                )
            }
            .addOnFailureListener { e ->
                onFailure("데이터를 불러오는 데 실패했어요: ${e.message}")
            }
    }
}