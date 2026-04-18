package com.example.emotionalapp.ui.body

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class BodyRecordRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun saveBodyRecord(
        context: Context,
        trainingId: String,
        feedbackText: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email
                ?: return Result.failure(IllegalStateException("로그인된 사용자를 찾을 수 없습니다."))

            val prefs = context.getSharedPreferences("body_training_records", Context.MODE_PRIVATE)
            val key = "feedback_$trainingId"
            prefs.edit().putString(key, feedbackText).apply()

            val nowTimestamp = Timestamp.now()
            val docId = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }.format(nowTimestamp.toDate())

            val record = hashMapOf(
                "content" to feedbackText,
                "date" to nowTimestamp,
                "trainingId" to trainingId
            )

            db.collection("user")
                .document(userEmail)
                .collection("bodyRecord")
                .document(docId)
                .set(record)
                .await()

            db.collection("user")
                .document(userEmail)
                .update("countComplete.$trainingId", FieldValue.increment(1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}