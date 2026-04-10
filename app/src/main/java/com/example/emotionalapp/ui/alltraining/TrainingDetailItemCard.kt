package com.example.emotionalapp.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionalapp.R
import com.example.emotionalapp.data.DetailTrainingItem
import com.example.emotionalapp.data.TrainingType

@Composable
fun TrainingDetailItemCard(
    item: DetailTrainingItem,
    onClick: () -> Unit
) {
    val backgroundColor =
        item.backgroundColorResId?.let { colorResource(id = it) } ?: Color(0xFFE91E63)

    val isLocked = item.currentProgress == "잠김"

    val progressPercent = if (isLocked) {
        0f
    } else {
        val numerator = item.progressNumerator.toFloatOrNull() ?: 0f
        val denominator = item.progressDenominator.toFloatOrNull() ?: 0f
        if (denominator > 0f) numerator / denominator else 0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = item.subtitle,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progressPercent.coerceIn(0f, 1f) },
                    modifier = Modifier.size(50.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeWidth = 4.dp
                )

                Text(
                    text = item.currentProgress,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingDetailItemCardPreview() {
    TrainingDetailItemCard(
        item = DetailTrainingItem(
            id = "1",
            title = "상태 기록하기",
            subtitle = "정서와 관련된 신체 감각 찾기",
            trainingType = TrainingType.EMOTION_TRAINING,
            progressNumerator = "3",
            progressDenominator = "14",
            currentProgress = "3/14",
            backgroundColorResId = R.color.button_color_emotion,
            targetActivityClass = null
        ),
        onClick = {}
    )
}