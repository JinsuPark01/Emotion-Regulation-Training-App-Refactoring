package com.example.emotionalapp.ui.expression.alternative

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AlternativeRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveTraining(state: AlternativeUiState): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val email = user.email ?: "unknown_user"
            val timestamp = Timestamp.now()

            val docId = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(timestamp.toDate())

            val data = hashMapOf(
                "answer1" to state.situation,
                "answer2" to state.selectedEmotion,
                "answer3" to if (state.selectedEmotion == "직접 입력") state.customEmotion else state.selectedDetailedEmotion,
                "answer4" to state.selectedAlternative,
                "answer5" to state.customAlternative,
                "answer6" to state.finalActionTaken,
                "date" to timestamp
            )

            db.collection("user")
                .document(email)
                .collection("expressionAlternative")
                .document(docId)
                .set(data)
                .await()

            db.collection("user")
                .document(email)
                .update("countComplete.alternative", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}