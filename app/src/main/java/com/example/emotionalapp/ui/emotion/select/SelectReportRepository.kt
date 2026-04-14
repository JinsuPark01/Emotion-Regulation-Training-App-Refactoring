package com.example.emotionalapp.ui.emotion.select

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class SelectReportRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun loadSelectReport(
        onSuccess: (SelectReportData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email.isNullOrEmpty()) {
            onFailure("로그인 정보를 확인할 수 없습니다.")
            return
        }

        db.collection("user")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                val signupTimestamp = document.getTimestamp("signupDate")
                if (signupTimestamp == null) {
                    onFailure("회원가입 날짜 정보를 찾을 수 없습니다.")
                    return@addOnSuccessListener
                }

                val signupDateMidnight = toMidnight(signupTimestamp.toDate())

                db.collection("user")
                    .document(email)
                    .collection("emotionSelect")
                    .get()
                    .addOnSuccessListener { result ->
                        val dataMap = mutableMapOf<Pair<Int, String>, MutableMap<String, Any>>()

                        for (doc in result) {
                            val dateTimestamp = doc.getTimestamp("date") ?: continue
                            val date = dateTimestamp.toDate()
                            val recordDateMidnight = toMidnight(date)

                            val diffDays =
                                ((recordDateMidnight.time - signupDateMidnight.time) / (1000L * 60 * 60 * 24)).toInt()

                            if (diffDays !in 0..6) continue

                            val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
                            cal.time = date
                            val hour = cal.get(Calendar.HOUR_OF_DAY)
                            val timeType = if (hour < 12) "11" else "19"

                            val key = Pair(diffDays, timeType)
                            val existing = dataMap[key]
                            val existingDate = existing?.get("timestamp") as? Date

                            if (existingDate == null || date.after(existingDate)) {
                                dataMap[key] = mutableMapOf(
                                    "mind" to (doc.getString("mind") ?: "보통"),
                                    "body" to (doc.getString("body") ?: "보통"),
                                    "timestamp" to date
                                )
                            }
                        }

                        val mindStates = listOf("매우 안 좋음", "안 좋음", "보통", "좋음", "매우 좋음")
                        val bodyStates = listOf("매우 이완됨", "이완됨", "보통", "각성", "매우 각성됨")

                        val mind11 = MutableList<Float?>(7) { null }
                        val mind19 = MutableList<Float?>(7) { null }
                        val body11 = MutableList<Float?>(7) { null }
                        val body19 = MutableList<Float?>(7) { null }

                        for ((key, value) in dataMap) {
                            val (dayIdx, time) = key
                            val mind = value["mind"] as String
                            val body = value["body"] as String

                            val mindVal = mindStates.indexOf(mind).takeIf { it >= 0 }?.toFloat() ?: 2f
                            val bodyVal = bodyStates.indexOf(body).takeIf { it >= 0 }?.toFloat() ?: 2f

                            when (time) {
                                "11" -> {
                                    mind11[dayIdx] = mindVal
                                    body11[dayIdx] = bodyVal
                                }

                                "19" -> {
                                    mind19[dayIdx] = mindVal
                                    body19[dayIdx] = bodyVal
                                }
                            }
                        }

                        onSuccess(
                            SelectReportData(
                                mind11 = mind11,
                                mind19 = mind19,
                                body11 = body11,
                                body19 = body19,
                                mindLabels = mindStates,
                                bodyLabels = bodyStates
                            )
                        )
                    }
                    .addOnFailureListener { e ->
                        onFailure("기록 데이터를 불러오는 데 실패했어요: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onFailure("회원 정보를 불러오는 데 실패했어요: ${e.message}")
            }
    }

    private fun toMidnight(date: Date): Date {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }
}