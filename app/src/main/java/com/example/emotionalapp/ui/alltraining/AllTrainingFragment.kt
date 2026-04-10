package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.emotionalapp.R
import com.example.emotionalapp.data.TrainingMenuItem
import com.example.emotionalapp.data.TrainingMenuType
import com.example.emotionalapp.ui.login_signup.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class AllTrainingFragment : Fragment() {

    private val trainingItems: List<TrainingMenuItem> by lazy {
        loadTrainingData()
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        return ComposeView(requireContext()).apply {
            setContent {
                AllTrainingScreen(
                    trainingItems = trainingItems,
                    onLogoutClick = { showLogoutDialog() },
                    onTabTodayClick = {
                        val intent = Intent(requireContext(), DailyTrainingPageActivity::class.java)
                        startActivity(intent)
                    },
                    onTabAllClick = {
                        Log.d("AllTrainingFragment", "전체 훈련 탭 클릭됨 (현재 페이지)")
                    },
                    onTrainingClick = { clickedTrainingItem ->
                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                TrainingDetailFragment.newInstance(clickedTrainingItem)
                            )
                            .addToBackStack(null)
                            .commit()
                    }
                )
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("로그아웃")
            .setMessage("정말 로그아웃하시겠어요?")
            .setPositiveButton("로그아웃") { _, _ ->
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun loadTrainingData(): List<TrainingMenuItem> {
        return listOf(
            TrainingMenuItem(
                id = "intro001",
                title = "INTRO",
                subtitle = "감정의 세계로 떠나는 첫 걸음",
                type = TrainingMenuType.INTRO,
                backgroundColorResId = R.color.button_color_intro
            ),
            TrainingMenuItem(
                id = "et001",
                title = "1주차 - 정서인식 훈련",
                subtitle = "나의 감정을 정확히 알아차리기",
                type = TrainingMenuType.EMOTION,
                backgroundColorResId = R.color.button_color_emotion
            ),
            TrainingMenuItem(
                id = "bt001",
                title = "2주차 - 신체자각 훈련",
                subtitle = "몸이 보내는 신호에 귀 기울이기",
                type = TrainingMenuType.BODY,
                backgroundColorResId = R.color.button_color_body
            ),
            TrainingMenuItem(
                id = "mwt001",
                title = "3주차 - 인지재구성 훈련",
                subtitle = "생각의 틀을 바꾸는 연습",
                type = TrainingMenuType.MIND,
                backgroundColorResId = R.color.button_color_mind
            ),
            TrainingMenuItem(
                id = "eat001",
                title = "4주차 - 정서표현 및 행동 훈련",
                subtitle = "건강하게 감정을 표현하고 행동하기",
                type = TrainingMenuType.EXPRESSION,
                backgroundColorResId = R.color.button_color_expression
            )
        )
    }
}