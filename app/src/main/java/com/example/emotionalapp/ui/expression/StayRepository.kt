package com.example.emotionalapp.ui.expression

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StayRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun isFirstTraining(): Result<Boolean> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
            val email = user.email
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))

            val documents = db.collection("user")
                .document(email)
                .collection("expressionStay")
                .limit(1)
                .get()
                .await()

            Result.success(documents.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTraining(state: StayUiState): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
            val email = user.email
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))

            val timestamp = Timestamp.now()
            val dateString = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(timestamp.toDate())

            val data = hashMapOf(
                "type" to "emotionStay",
                "date" to timestamp,
                "emotion" to state.selectedEmotion,
                "answer1" to state.clarifiedEmotion,
                "answer2" to state.moodChanged
            )

            db.collection("user")
                .document(email)
                .collection("expressionStay")
                .document(dateString)
                .set(data)
                .await()

            db.collection("user")
                .document(email)
                .update("countComplete.stay", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}