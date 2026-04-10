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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    recordItems: List<DetailTrainingItem>,
    trainingItems: List<DetailTrainingItem>,
    onBackClick: () -> Unit,
    onRecordTabClick: () -> Unit,
    onTrainingTabClick: () -> Unit,
    onDetailItemClick: (DetailTrainingItem) -> Unit
) {
    val selectedTab = remember { mutableStateOf(DetailTab.TRAINING) }

    val currentItems = if (selectedTab.value == DetailTab.RECORD) {
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
            selectedTab = selectedTab.value,
            onRecordTabClick = {
                selectedTab.value = DetailTab.RECORD
                onRecordTabClick()
            },
            onTrainingTabClick = {
                selectedTab.value = DetailTab.TRAINING
                onTrainingTabClick()
            }
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
    selectedTab: DetailTab,
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
                selected = selectedTab == DetailTab.RECORD,
                onClick = onRecordTabClick,
                modifier = Modifier.weight(1f)
            )

            DetailTabItem(
                text = "훈련 하기",
                selected = selectedTab == DetailTab.TRAINING,
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

enum class DetailTab {
    RECORD,
    TRAINING
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingDetailScreenPreview() {
    val recordItems = listOf(
        DetailTrainingItem(
            id = "r1",
            title = "기록 보기 1",
            subtitle = "기록 예시입니다",
            trainingType = TrainingType.EMOTION_TRAINING,
            progressNumerator = "1",
            progressDenominator = "4",
            currentProgress = "1/4",
            backgroundColorResId = R.color.button_color_emotion,
            targetActivityClass = null
        ),
        DetailTrainingItem(
            id = "r2",
            title = "기록 보기 2",
            subtitle = "기록 예시입니다",
            trainingType = TrainingType.EMOTION_TRAINING,
            progressNumerator = "2",
            progressDenominator = "4",
            currentProgress = "2/4",
            backgroundColorResId = R.color.button_color_emotion,
            targetActivityClass = null
        )
    )

    val trainingItems = listOf(
        DetailTrainingItem(
            id = "t1",
            title = "훈련 하기 1",
            subtitle = "훈련 예시입니다",
            trainingType = TrainingType.EMOTION_TRAINING,
            progressNumerator = "3",
            progressDenominator = "14",
            currentProgress = "3/14",
            backgroundColorResId = R.color.button_color_emotion,
            targetActivityClass = null
        ),
        DetailTrainingItem(
            id = "t2",
            title = "훈련 하기 2",
            subtitle = "훈련 예시입니다",
            trainingType = TrainingType.EMOTION_TRAINING,
            progressNumerator = "잠김",
            progressDenominator = "잠김",
            currentProgress = "잠김",
            backgroundColorResId = R.color.button_color_emotion,
            targetActivityClass = null
        )
    )

    TrainingDetailScreen(
        pageTitle = "1주차 - 정서인식 훈련",
        recordItems = recordItems,
        trainingItems = trainingItems,
        onBackClick = {},
        onRecordTabClick = {},
        onTrainingTabClick = {},
        onDetailItemClick = {}
    )
}