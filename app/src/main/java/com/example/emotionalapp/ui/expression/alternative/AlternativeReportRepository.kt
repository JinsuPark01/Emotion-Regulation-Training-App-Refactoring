package com.example.emotionalapp.ui.expression.alternative

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AlternativeReportRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun loadAlternativeReport(
        reportDateMillis: Long,
        onSuccess: (AlternativeReportData) -> Unit,
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
            .collection("expressionAlternative")
            .whereEqualTo("date", reportTimestamp)
            .get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()

                if (doc == null) {
                    onFailure("해당 기록을 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                val input1 = doc.getString("answer1") ?: "기록된 내용이 없습니다."
                val input2 = doc.getString("answer2") ?: "기록된 내용이 없습니다."
                val input3 = doc.getString("answer3") ?: "기록된 내용이 없습니다."
                val input4 = doc.getString("answer4") ?: "기록된 내용이 없습니다."
                val input5 = doc.getString("answer5") ?: "기록된 내용이 없습니다."
                val input6 = doc.getString("answer6") ?: "기록된 내용이 없습니다."

                val reportData = if (input2 == "직접 입력") {
                    AlternativeReportData(
                        answer1 = input1,
                        answer2 = input3,
                        answer3 = "세부 감정이 없습니다.",
                        answer4 = "기록된 내용이 없습니다",
                        answer5 = input5,
                        answer6 = input6
                    )
                } else {
                    AlternativeReportData(
                        answer1 = input1,
                        answer2 = input2,
                        answer3 = input3,
                        answer4 = input4,
                        answer5 = input5,
                        answer6 = input6
                    )
                }

                onSuccess(reportData)
            }
            .addOnFailureListener { e ->
                onFailure("데이터를 불러오는 데 실패했어요: ${e.message}")
            }
    }
}