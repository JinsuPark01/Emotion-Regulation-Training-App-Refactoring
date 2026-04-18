package com.example.emotionalapp.ui.mind.trap

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

class TrapRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun saveMindTrapTraining(
        state: TrapUiState
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
                "type" to "mindTrap",
                "date" to nowTimestamp,
                "situation" to state.responsePage1Answer1,
                "thought" to state.responsePage1Answer2,
                "trap" to state.responsePage2Text,
                "validity" to hashMapOf(
                    "answer1" to state.responsePage4ZeroAnswers[0],
                    "answer2" to state.responsePage4ZeroAnswers[1],
                    "answer3" to state.responsePage4ZeroAnswers[2],
                    "answer4" to state.responsePage4ZeroAnswers[3]
                ),
                "assumption" to hashMapOf(
                    "answer1" to state.responsePage4OneAnswers[0],
                    "answer2" to state.responsePage4OneAnswers[1],
                    "answer3" to state.responsePage4OneAnswers[2],
                    "answer4" to state.responsePage4OneAnswers[3]
                ),
                "perspective" to hashMapOf(
                    "answer1" to state.responsePage4TwoAnswers[0],
                    "answer2" to state.responsePage4TwoAnswers[1],
                    "answer3" to state.responsePage4TwoAnswers[2]
                ),
                "alternative" to state.responsePage6Text
            )

            val userRef = db.collection("user").document(userEmail)

            userRef.collection("mindTrap")
                .document(today)
                .set(data)
                .await()

            userRef.update("countComplete.trap", FieldValue.increment(1))
                .await()

            Log.d("TrapRepository", "mindTrap 저장 및 countComplete.trap 증가 성공")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TrapRepository", "saveMindTrapTraining 실패", e)
            Result.failure(e)
        }
    }
}