package com.example.emotionalapp.ui.alltraining

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
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
}