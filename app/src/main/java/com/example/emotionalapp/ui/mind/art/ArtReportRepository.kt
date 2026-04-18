package com.example.emotionalapp.ui.mind.art

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import javax.inject.Inject

class ArtReportRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun loadArtReport(
        reportDateMillis: Long,
        onSuccess: (ArtReportData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val email = auth.currentUser?.email
        if (email.isNullOrEmpty()) {
            onFailure("로그인 정보를 확인할 수 없습니다.")
            return
        }

        if (reportDateMillis == -1L) {
            onFailure("보고서 날짜가 유효하지 않습니다.")
            return
        }

        val targetTimestamp = Timestamp(Date(reportDateMillis))

        db.collection("user")
            .document(email)
            .collection("mindArt")
            .whereEqualTo("date", targetTimestamp)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    onFailure("해당 날짜의 보고서를 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                val document = documents.first()

                val firstAnswers = (1..5).map { index ->
                    document.getString("1art_$index") ?: ""
                }

                val secondAnswers = (1..5).map { index ->
                    document.getString("2art_$index") ?: ""
                }

                onSuccess(
                    ArtReportData(
                        firstImageName = document.getString("firstImage") ?: "",
                        secondImageName = document.getString("secondImage") ?: "",
                        firstAnswers = firstAnswers,
                        secondAnswers = secondAnswers
                    )
                )
            }
            .addOnFailureListener {
                onFailure("데이터 로드 실패: ${it.message}")
            }
    }
}