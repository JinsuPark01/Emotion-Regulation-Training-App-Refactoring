package com.example.emotionalapp.ui.mind

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AutoTrapReportRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    private val trapOptions = listOf(
        "성급하게 결론짓기",
        "최악을 생각하기",
        "긍정적인 면 무시하기",
        "흑백사고",
        "점쟁이 사고 (지레짐작하기)",
        "독심술",
        "정서적 추리",
        "꼬리표 붙이기",
        "“해야만 한다“는 진술문",
        "마술적 사고"
    )

    fun loadTrapStatistics(
        onSuccess: (AutoTrapReportData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userEmail = auth.currentUser?.email

        if (userEmail.isNullOrEmpty()) {
            onFailure("로그인 정보를 확인할 수 없습니다.")
            return
        }

        val trapCountMap = mutableMapOf<String, Int>().apply {
            trapOptions.forEach { this[it] = 0 }
        }

        val trapTask = db.collection("user").document(userEmail).collection("mindTrap").get()
        val autoTask = db.collection("user").document(userEmail).collection("mindAuto").get()

        Tasks.whenAllSuccess<Any>(trapTask, autoTask)
            .addOnSuccessListener { results ->
                trapCountMap.keys.forEach { trapCountMap[it] = 0 }

                for (result in results) {
                    if (result is com.google.firebase.firestore.QuerySnapshot) {
                        for (doc in result.documents) {
                            val trapValue = doc.getString("trap")
                            trapValue?.let { trapStr ->
                                trapOptions.forEachIndexed { index, option ->
                                    if (trapStr.contains(option)) {
                                        val shortKey = trapOptions[index]
                                        trapCountMap[shortKey] = (trapCountMap[shortKey] ?: 0) + 1
                                    }
                                }
                            }
                        }
                    }
                }

                val counts = trapOptions.map { option -> trapCountMap[option] ?: 0 }

                onSuccess(
                    AutoTrapReportData(
                        trapOptions = trapOptions,
                        trapCounts = counts
                    )
                )
            }
            .addOnFailureListener { e ->
                onFailure("통계 데이터를 불러오지 못했습니다: ${e.message}")
            }
    }
}