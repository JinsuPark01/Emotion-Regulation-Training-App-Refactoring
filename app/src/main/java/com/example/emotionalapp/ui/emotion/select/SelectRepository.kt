package com.example.emotionalapp.ui.emotion.select

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class SelectRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun checkSelectableState(): Result<SelectAvailabilityResult> {
        return try {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            val timeSlot = when (hour) {
                in 5..13 -> "morning"
                in 14..22 -> "evening"
                else -> null
            }

            if (timeSlot == null) {
                return Result.success(
                    SelectAvailabilityResult(
                        isEnabled = false,
                        buttonText = "기록은 11~12시, 19~20시에만 가능합니다."
                    )
                )
            }

            val user = auth.currentUser
            val email = user?.email
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))

            val snapshot = db.collection("user")
                .document(email)
                .collection("emotionSelect")
                .whereGreaterThanOrEqualTo("date", getTimeSlotStart(timeSlot))
                .whereLessThan("date", getTimeSlotEnd(timeSlot))
                .get()
                .await()

            if (!snapshot.isEmpty) {
                Result.success(
                    SelectAvailabilityResult(
                        isEnabled = false,
                        buttonText = "해당 시간 상태 기록이 완료되었습니다."
                    )
                )
            } else {
                Result.success(
                    SelectAvailabilityResult(
                        isEnabled = true,
                        buttonText = "상태 기록하기"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveEmotionSelection(
        selectedMind: Int,
        selectedBody: Int
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val email = user?.email
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))

            val mindStates = listOf("매우 안 좋음", "안 좋음", "보통", "좋음", "매우 좋음")
            val bodyStates = listOf("매우 이완됨", "이완됨", "보통", "각성", "매우 각성됨")

            val mind = mindStates.getOrNull(selectedMind)
                ?: return Result.failure(IllegalArgumentException("마음 상태를 선택해주세요."))
            val body = bodyStates.getOrNull(selectedBody)
                ?: return Result.failure(IllegalArgumentException("몸 상태를 선택해주세요."))

            val timestamp = Timestamp.now()

            val idFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }
            val timestampStr = idFormat.format(timestamp.toDate())

            val data = hashMapOf(
                "mind" to mind,
                "body" to body,
                "date" to timestamp
            )

            db.collection("user")
                .document(email)
                .collection("emotionSelect")
                .document(timestampStr)
                .set(data)
                .await()

            db.collection("user")
                .document(email)
                .update("countComplete.select", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getTimeSlotStart(slot: String): Timestamp {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (slot == "morning") {
                set(Calendar.HOUR_OF_DAY, 5)
            } else {
                set(Calendar.HOUR_OF_DAY, 14)
            }
        }
        return Timestamp(calendar.time)
    }

    private fun getTimeSlotEnd(slot: String): Timestamp {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (slot == "morning") {
                set(Calendar.HOUR_OF_DAY, 13)
            } else {
                set(Calendar.HOUR_OF_DAY, 22)
            }
        }
        return Timestamp(calendar.time)
    }
}

data class SelectAvailabilityResult(
    val isEnabled: Boolean,
    val buttonText: String
)