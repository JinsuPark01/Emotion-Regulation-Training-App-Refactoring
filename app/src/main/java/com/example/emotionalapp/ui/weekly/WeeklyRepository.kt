package com.example.emotionalapp.ui.weekly

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class WeeklyRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun saveWeeklyTraining(state: WeeklyUiState): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))

            val nowTimestamp = Timestamp.now()
            val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(nowTimestamp.toDate())

            val data = hashMapOf(
                "type" to "weekly3",
                "date" to nowTimestamp,
                "phq9" to hashMapOf(
                    "answers" to state.phq9Selections,
                    "sum" to state.phq9Sum
                ),
                "gad7" to hashMapOf(
                    "answers" to state.gad7Selections,
                    "sum" to state.gad7Sum
                ),
                "panas" to hashMapOf(
                    "answers" to state.panasSelections,
                    "positiveSum" to state.panasPositiveSum,
                    "negativeSum" to state.panasNegativeSum
                )
            )

            db.collection("user")
                .document(userEmail)
                .collection("weekly3")
                .document(today)
                .set(data)
                .await()

            db.collection("user")
                .document(userEmail)
                .update("countComplete.weekly", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("WeeklyRepository", "저장 실패", e)
            Result.failure(e)
        }
    }
}