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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType
import com.example.emotionalapp.ui.detail.TrainingDetailItemCard

@Composable
fun TrainingDetailScreen(
    pageTitle: String,
    selectedTab: TrainingDetailTab,
    recordItems: List<DetailTrainingItem>,
    trainingItems: List<DetailTrainingItem>,
    onBackClick: () -> Unit,
    onRecordTabClick: () -> Unit,
    onTrainingTabClick: () -> Unit,
    onDetailItemClick: (DetailTrainingItem) -> Unit
) {
    val currentItems = if (selectedTab == TrainingDetailTab.RECORD) {
        recordItems
    } else {
        trainingItems
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        DetailTopBar(
            pageTitle = pageTitle,
            onBackClick = onBackClick
        )

        DetailTabSection(
            selectedTab = selectedTab,
            onRecordTabClick = onRecordTabClick,
            onTrainingTabClick = onTrainingTabClick
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF5F5F5))
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(currentItems) { item ->
                TrainingDetailItemCard(
                    item = item,
                    onClick = { onDetailItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun DetailTopBar(
    pageTitle: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "뒤로가기",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = pageTitle,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun DetailTabSection(
    selectedTab: TrainingDetailTab,
    onRecordTabClick: () -> Unit,
    onTrainingTabClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            DetailTabItem(
                text = "기록 보기",
                selected = selectedTab == TrainingDetailTab.RECORD,
                onClick = onRecordTabClick,
                modifier = Modifier.weight(1f)
            )

            DetailTabItem(
                text = "훈련 하기",
                selected = selectedTab == TrainingDetailTab.TRAINING,
                onClick = onTrainingTabClick,
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
private fun DetailTabItem(
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingDetailScreenPreview() {
    val recordItems = listOf(
        DetailTrainingItem(
            id = "r1",
            title = "2026-04-11",
            subtitle = "인지적 평가",
            trainingType = TrainingType.MIND_WATCHING_TRAINING,
            progressNumerator = "1",
            progressDenominator = "1",
            currentProgress = "보기",
            backgroundColorResId = R.color.button_color_mind,
            targetActivityClass = null
        ),
        DetailTrainingItem(
            id = "r2",
            title = "2026-04-10",
            subtitle = "생각의 덫",
            trainingType = TrainingType.MIND_WATCHING_TRAINING,
            progressNumerator = "1",
            progressDenominator = "1",
            currentProgress = "보기",
            backgroundColorResId = R.color.button_color_mind,
            targetActivityClass = null
        )
    )

    val trainingItems = listOf(
        DetailTrainingItem(
            id = "t1",
            title = "인지적 평가",
            subtitle = "인지적 평가 교육 및 모호한 그림 해석을 진행합니다.",
            trainingType = TrainingType.MIND_WATCHING_TRAINING,
            progressNumerator = "3",
            progressDenominator = "14",
            currentProgress = "3/14",
            backgroundColorResId = R.color.button_color_mind,
            targetActivityClass = null
        ),
        DetailTrainingItem(
            id = "t2",
            title = "생각의 덫",
            subtitle = "생각의 덫을 파악하고 풀어내봅시다.",
            trainingType = TrainingType.MIND_WATCHING_TRAINING,
            progressNumerator = "잠김",
            progressDenominator = "잠김",
            currentProgress = "잠김",
            backgroundColorResId = R.color.button_color_mind,
            targetActivityClass = null
        )
    )

    TrainingDetailScreen(
        pageTitle = "마음보기 훈련",
        selectedTab = TrainingDetailTab.TRAINING,
        recordItems = recordItems,
        trainingItems = trainingItems,
        onBackClick = {},
        onRecordTabClick = {},
        onTrainingTabClick = {},
        onDetailItemClick = {}
    )
}