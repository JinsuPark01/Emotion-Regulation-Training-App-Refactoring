package com.example.emotionalapp.ui.main

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingFragment
import com.example.emotionalapp.ui.chat.ChatFragment

class MainActivity : AppCompatActivity() {

    private var currentTab: BottomTab = BottomTab.MY_TRAINING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            switchFragment(BottomTab.MY_TRAINING)
        } else {
            updateBottomNavUI(currentTab)
        }
    }

    private fun setupBottomNavigation() {
        val btnDashboard = findViewById<LinearLayout>(R.id.nav_dashboard)
        val btnMyTraining = findViewById<LinearLayout>(R.id.nav_my_training)
        val btnChat = findViewById<LinearLayout>(R.id.nav_chat)
        val btnSetting = findViewById<LinearLayout>(R.id.nav_setting)

        btnDashboard.setOnClickListener {
            Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show()
        }

        btnMyTraining.setOnClickListener {
            if (currentTab != BottomTab.MY_TRAINING) {
                switchFragment(BottomTab.MY_TRAINING)
            }
        }

        btnChat.setOnClickListener {
            if (currentTab != BottomTab.CHAT) {
                switchFragment(BottomTab.CHAT)
            }
        }

        btnSetting.setOnClickListener {
            Toast.makeText(this, "미구현 기능입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchFragment(tab: BottomTab) {
        currentTab = tab

        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val fragment = when (tab) {
            BottomTab.MY_TRAINING -> AllTrainingFragment()
            BottomTab.CHAT -> ChatFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateBottomNavUI(tab)
    }

    private fun updateBottomNavUI(selectedTab: BottomTab) {
        val activeColor = android.graphics.Color.parseColor("#00897B")
        val defaultColor = android.graphics.Color.BLACK

        val tvMyTraining = findViewById<TextView>(R.id.tv_my_training)
        val ivMyTraining = findViewById<ImageView>(R.id.iv_my_training)

        val tvChat = findViewById<TextView>(R.id.tv_chat)
        val ivChat = findViewById<ImageView>(R.id.iv_chat)

        // 기본값으로 초기화
        tvMyTraining.setTextColor(defaultColor)
        ivMyTraining.setColorFilter(defaultColor)

        tvChat.setTextColor(defaultColor)
        ivChat.setColorFilter(defaultColor)

        // 선택 탭만 활성화 색상 적용
        when (selectedTab) {
            BottomTab.MY_TRAINING -> {
                tvMyTraining.setTextColor(activeColor)
                ivMyTraining.setColorFilter(activeColor)
            }
            BottomTab.CHAT -> {
                tvChat.setTextColor(activeColor)
                ivChat.setColorFilter(activeColor)
            }
        }
    }
}

enum class BottomTab {
    MY_TRAINING,
    CHAT
}