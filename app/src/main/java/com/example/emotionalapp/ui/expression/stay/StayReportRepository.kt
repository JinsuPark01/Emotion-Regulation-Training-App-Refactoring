package com.example.emotionalapp.ui.expression.stay

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import javax.inject.Inject

class StayReportRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun loadStayReport(
        reportDateMillis: Long,
        onSuccess: (StayReportData) -> Unit,
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
            .collection("expressionStay")
            .whereEqualTo("date", reportTimestamp)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()

                if (doc == null) {
                    onFailure("해당 기록을 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                onSuccess(
                    StayReportData(
                        emotion = doc.getString("emotion") ?: "기록된 감정이 없습니다.",
                        answer1 = doc.getString("answer1") ?: "기록된 내용이 없습니다.",
                        answer2 = doc.getString("answer2") ?: "기록된 내용이 없습니다."
                    )
                )
            }
            .addOnFailureListener { e ->
                onFailure("데이터를 불러오는 데 실패했어요: ${e.message}")
            }
    }
}