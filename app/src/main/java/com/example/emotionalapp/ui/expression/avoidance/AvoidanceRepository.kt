package com.example.emotionalapp.ui.expression.avoidance

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AvoidanceRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveAvoidanceTraining(
        state: AvoidanceUiState,
        selectedAvoidanceTexts: List<String>
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email
                ?: return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))

            val now = Timestamp.now()
            val dateString = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("Asia/Seoul") }
                .format(now.toDate())

            val avoid1 = selectedAvoidanceTexts.firstOrNull().orEmpty()
            val avoid2 = state.customAvoidance

            val data = hashMapOf(
                "date" to now,
                "avoid1" to avoid1,
                "avoid2" to avoid2,
                "answer1" to state.situation,
                "answer2" to state.emotion,
                "answer3" to state.method,
                "result4" to state.result,
                "effect" to state.effect
            )

            db.collection("user")
                .document(userEmail)
                .collection("expressionAvoidance")
                .document(dateString)
                .set(data)
                .await()

            db.collection("user")
                .document(userEmail)
                .update("countComplete.avoidance", FieldValue.increment(1))
                .await()

            Log.d("AvoidanceRepository", "expressionAvoidance 저장 및 countComplete.avoidance 증가 성공")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("AvoidanceRepository", "저장 실패", e)
            Result.failure(e)
        }
    }
}