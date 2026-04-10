package com.example.emotionalapp.ui.alltraining

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionalapp.R
import com.example.emotionalapp.data.TrainingMenuItem
import com.example.emotionalapp.data.TrainingMenuType

@Composable
fun AllTrainingScreen(
    trainingItems: List<TrainingMenuItem>,
    onLogoutClick: () -> Unit,
    onTabTodayClick: () -> Unit,
    onTabAllClick: () -> Unit,
    onTrainingClick: (TrainingMenuItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        AllTrainingTopBar(
            onLogoutClick = onLogoutClick
        )

        AllTrainingTabSection(
            selectedTab = AllTrainingTab.ALL,
            onTabTodayClick = onTabTodayClick,
            onTabAllClick = onTabAllClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF5F5F5))
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trainingItems) { item ->
                TrainingItemCard(
                    item = item,
                    onClick = { onTrainingClick(item) }
                )
            }
        }
    }
}

@Composable
private fun AllTrainingTopBar(
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "나의 훈련",
            modifier = Modifier.weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = "로그아웃",
            modifier = Modifier
                .size(24.dp)
                .clickable { onLogoutClick() },
            tint = Color.Black
        )
    }
}

@Composable
private fun AllTrainingTabSection(
    selectedTab: AllTrainingTab,
    onTabTodayClick: () -> Unit,
    onTabAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            AllTrainingTabItem(
                text = "훈련 일정",
                selected = selectedTab == AllTrainingTab.TODAY,
                onClick = onTabTodayClick,
                modifier = Modifier.weight(1f)
            )

            AllTrainingTabItem(
                text = "전체 훈련",
                selected = selectedTab == AllTrainingTab.ALL,
                onClick = onTabAllClick,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFDDDDDD)
        )
    }
}

@Composable
private fun AllTrainingTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        HorizontalDivider(
            thickness = 2.dp,
            color = if (selected) Color.Black else Color.Transparent
        )
    }
}

private enum class AllTrainingTab {
    TODAY,
    ALL
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AllTrainingScreenPreview() {
    val sampleItems = listOf(
        TrainingMenuItem(
            id = "1",
            title = "대표 훈련 시작하기",
            subtitle = "가장 중요한 훈련을 바로 경험해보세요",
            type = TrainingMenuType.INTRO,
            backgroundColorResId = R.color.pink
        ),
        TrainingMenuItem(
            id = "2",
            title = "감정 훈련",
            subtitle = "오늘의 감정을 기록해보세요",
            type = TrainingMenuType.EMOTION,
            backgroundColorResId = R.color.purple_500
        ),
        TrainingMenuItem(
            id = "3",
            title = "행동 훈련",
            subtitle = "행동 패턴을 점검해보세요",
            type = TrainingMenuType.MIND,
            backgroundColorResId = R.color.pink
        )
    )

    AllTrainingScreen(
        trainingItems = sampleItems,
        onLogoutClick = {},
        onTabTodayClick = {},
        onTabAllClick = {},
        onTrainingClick = {}
    )
}