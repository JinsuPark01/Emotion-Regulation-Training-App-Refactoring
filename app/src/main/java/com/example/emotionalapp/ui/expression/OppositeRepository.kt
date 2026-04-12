package com.example.emotionalapp.ui.expression

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OppositeRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun saveTraining(state: OppositeUiState): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val email = user.email ?: "unknown_user"
            val timestamp = Timestamp.now()

            val docId = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(timestamp.toDate())

            val data = hashMapOf(
                "answer1" to state.answer1,
                "answer2" to state.answer2,
                "answer3" to state.answer3,
                "answer5" to state.answer5,
                "date" to timestamp
            )

            db.collection("user").document(email)
                .collection("expressionOpposite")
                .document(docId)
                .set(data)
                .await()

            db.collection("user")
                .document(email)
                .update("countComplete.opposite", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}