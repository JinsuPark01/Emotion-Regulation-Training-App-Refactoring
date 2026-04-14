package com.example.emotionalapp.ui.mind.auto

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AutoReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun loadAutoReport(
        reportDateMillis: Long,
        onSuccess: (AutoReportData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (reportDateMillis == -1L) {
            onFailure("잘못된 접근입니다.")
            return
        }

        val user = auth.currentUser
        val email = user?.email

        if (user == null || email.isNullOrEmpty()) {
            onFailure("로그인 정보를 확인할 수 없습니다.")
            return
        }

        val targetDate = Date(reportDateMillis)

        db.collection("user")
            .document(email)
            .collection("mindAuto")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val timeStamp = document.getTimestamp("date")?.toDate()
                    if (timeStamp != null && timeStamp.time == targetDate.time) {
                        onSuccess(
                            AutoReportData(
                                answer1 = document.getString("answer1") ?: "",
                                answer2 = document.getString("answer2") ?: "",
                                answer3 = document.getString("answer3") ?: "",
                                answer5 = document.getString("answer5") ?: "",
                                trap = document.getString("trap") ?: ""
                            )
                        )
                        return@addOnSuccessListener
                    }
                }

                onFailure("기록을 찾을 수 없습니다.")
            }
            .addOnFailureListener {
                onFailure("기록을 불러오는데 실패했습니다.")
            }
    }
}