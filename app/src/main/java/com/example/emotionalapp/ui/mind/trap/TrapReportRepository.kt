package com.example.emotionalapp.ui.mind.trap

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class TrapReportRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun loadTrapReport(
        reportDateMillis: Long,
        onSuccess: (TrapReportData) -> Unit,
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
            .collection("mindTrap")
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

                val validityMap = doc.get("validity") as? Map<*, *>
                val assumptionMap = doc.get("assumption") as? Map<*, *>
                val perspectiveMap = doc.get("perspective") as? Map<*, *>

                onSuccess(
                    TrapReportData(
                        dateText = dateString,
                        situation = doc.getString("situation") ?: "",
                        thought = doc.getString("thought") ?: "",
                        trap = doc.getString("trap") ?: "",
                        alternative = doc.getString("alternative") ?: "",

                        showValidity = !isSectionEmpty(validityMap),
                        validityAnswers = listOf(
                            validityMap?.get("answer1") as? String ?: "",
                            validityMap?.get("answer2") as? String ?: "",
                            validityMap?.get("answer3") as? String ?: "",
                            validityMap?.get("answer4") as? String ?: ""
                        ),

                        showAssumption = !isSectionEmpty(assumptionMap),
                        assumptionAnswers = listOf(
                            assumptionMap?.get("answer1") as? String ?: "",
                            assumptionMap?.get("answer2") as? String ?: "",
                            assumptionMap?.get("answer3") as? String ?: "",
                            assumptionMap?.get("answer4") as? String ?: ""
                        ),

                        showPerspective = !isSectionEmpty(perspectiveMap),
                        perspectiveAnswers = listOf(
                            perspectiveMap?.get("answer1") as? String ?: "",
                            perspectiveMap?.get("answer2") as? String ?: "",
                            perspectiveMap?.get("answer3") as? String ?: ""
                        )
                    )
                )
            }
            .addOnFailureListener { e ->
                onFailure("데이터를 불러오는 데 실패했어요: ${e.message}")
            }
    }

    private fun isSectionEmpty(section: Map<*, *>?): Boolean {
        if (section == null) return true
        return section.values.all { (it as? String)?.isBlank() != false }
    }
}