package com.example.emotionalapp.ui.weekly

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WeeklyReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun loadWeeklyReport(
        reportDateMillis: Long,
        onSuccess: (WeeklyReportData) -> Unit,
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
            .collection("weekly3")
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

                val gad7Map = doc.get("gad7") as? Map<*, *>
                val gad7Sum = (gad7Map?.get("sum") as? Number)?.toInt() ?: -1

                val panasMap = doc.get("panas") as? Map<*, *>
                val positiveSum = (panasMap?.get("positiveSum") as? Number)?.toInt() ?: -1
                val negativeSum = (panasMap?.get("negativeSum") as? Number)?.toInt() ?: -1

                val phq9Map = doc.get("phq9") as? Map<*, *>
                val phq9Sum = (phq9Map?.get("sum") as? Number)?.toInt() ?: -1

                onSuccess(
                    WeeklyReportData(
                        dateText = dateString,
                        phq9Sum = phq9Sum,
                        gad7Sum = gad7Sum,
                        positiveSum = positiveSum,
                        negativeSum = negativeSum
                    )
                )
            }
            .addOnFailureListener { e ->
                onFailure("데이터를 불러오는 데 실패했어요: ${e.message}")
            }
    }
}