package com.example.emotionalapp.ui.mind

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AutoRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveAutoTraining(
        state: AutoUiState
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email

            if (user == null || userEmail == null) {
                return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
            }

            val timestamp = Timestamp.now()

            val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }
            val docId = sdf.format(timestamp.toDate())

            val data = hashMapOf(
                "answer1" to state.answerList[0],
                "answer2" to state.answerList[1],
                "answer3" to state.answerList[2],
                "trap" to state.selectedTrapText,
                "answer5" to state.answerList[4],
                "date" to timestamp
            )

            db.collection("user").document(userEmail)
                .collection("mindAuto").document(docId)
                .set(data)
                .await()

            db.collection("user").document(userEmail)
                .update("countComplete.auto", FieldValue.increment(1))
                .await()

            Log.d("AutoRepository", "mindAuto 저장 및 countComplete.auto 증가 성공")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AutoRepository", "saveAutoTraining 실패", e)
            Result.failure(e)
        }
    }
}