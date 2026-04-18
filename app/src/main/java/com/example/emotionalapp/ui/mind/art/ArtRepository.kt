package com.example.emotionalapp.ui.mind.art

import android.util.Log
import com.example.emotionalapp.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ArtRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    suspend fun saveArtTraining(
        selectedImageResourceIds: List<Int>,
        userAnswers: List<List<String>>
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
            val userEmail = user?.email

            if (user == null || userEmail == null) {
                return Result.failure(IllegalStateException("로그인 정보를 확인할 수 없습니다."))
            }

            val timestamp = Timestamp.now()

            val firstImageName = getImageNameByResId(selectedImageResourceIds.getOrNull(0) ?: 0)
            val secondImageName = getImageNameByResId(selectedImageResourceIds.getOrNull(1) ?: 0)

            val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
            }
            val docId = sdf.format(timestamp.toDate())

            val data = hashMapOf<String, Any>(
                "firstImage" to firstImageName,
                "secondImage" to secondImageName,
                "date" to timestamp
            )

            userAnswers[0].forEachIndexed { index, answer ->
                data["1art_${index + 1}"] = answer
            }

            userAnswers[1].forEachIndexed { index, answer ->
                data["2art_${index + 1}"] = answer
            }

            db.collection("user").document(userEmail)
                .collection("mindArt").document(docId)
                .set(data)
                .await()

            db.collection("user").document(userEmail)
                .update("countComplete.art", FieldValue.increment(1))
                .await()

            Log.d("ArtRepository", "mindArt 저장 및 countComplete.art 증가 성공")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ArtRepository", "saveArtTraining 실패", e)
            Result.failure(e)
        }
    }

    private fun getImageNameByResId(resId: Int): String {
        return when (resId) {
            R.drawable.art1 -> "art1"
            R.drawable.art2 -> "art2"
            R.drawable.art3 -> "art3"
            R.drawable.art4 -> "art4"
            R.drawable.art5 -> "art5"
            R.drawable.art6 -> "art6"
            R.drawable.art7 -> "art7"
            R.drawable.art8 -> "art8"
            R.drawable.art9 -> "art9"
            R.drawable.art10 -> "art10"
            R.drawable.art11 -> "art11"
            R.drawable.art12 -> "art12"
            else -> ""
        }
    }
}