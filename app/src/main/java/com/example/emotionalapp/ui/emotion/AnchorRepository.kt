package com.example.emotionalapp.ui.emotion

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AnchorRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveAnchorTraining(
        state: AnchorUiState,
        optionsQ1: List<String>,
        optionsQ2: List<String>
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email

            if (user == null || userEmail == null) {
                return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
            }

            val nowTimestamp = Timestamp.now()
            val today = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(nowTimestamp.toDate())

            val data = hashMapOf(
                "type" to "emotionAnchor",
                "date" to nowTimestamp,
                "selectedCue" to state.selectedCue,
                "elements" to hashMapOf(
                    "thought" to state.page2Answer1,
                    "sensation" to state.page2Answer2,
                    "behavior" to state.page2Answer3
                ),
                "evaluation" to hashMapOf(
                    "effect" to optionsQ1.getOrNull(state.selectedQ1Index),
                    "change" to optionsQ2.getOrNull(state.selectedQ2Index)
                )
            )

            db.collection("user")
                .document(userEmail)
                .collection("emotionAnchor")
                .document(today)
                .set(data)
                .await()

            db.collection("user")
                .document(userEmail)
                .update("countComplete.anchor", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("AnchorRepository", "저장 실패", e)
            Result.failure(e)
        }
    }
}