package com.example.emotionalapp.ui.emotion.arc

import android.util.Log
import com.example.emotionalapp.ui.emotion.arc.ArcUiState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ArcRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun saveArcTraining(
        state: ArcUiState,
        optionsQ1: List<String>,
        optionsQ2: List<String>
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email

            if (user == null || userEmail == null) {
                return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
            }

            val nowTimestamp = Timestamp.Companion.now()
            val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(nowTimestamp.toDate())

            val data = hashMapOf(
                "type" to "emotionArc",
                "date" to nowTimestamp,
                "antecedent" to state.userAntecedent,
                "response" to state.userResponse,
                "consequences" to hashMapOf(
                    "short" to state.userShortConsequence,
                    "long" to state.userLongConsequence
                ),
                "evaluation" to hashMapOf(
                    "effect" to optionsQ1.getOrNull(state.selectedQ1Index),
                    "change" to optionsQ2.getOrNull(state.selectedQ2Index)
                )
            )

            db.collection("user")
                .document(userEmail)
                .collection("emotionArc")
                .document(today)
                .set(data)
                .await()

            db.collection("user")
                .document(userEmail)
                .update("countComplete.arc", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("ArcRepository", "저장 실패", e)
            Result.failure(e)
        }
    }
}