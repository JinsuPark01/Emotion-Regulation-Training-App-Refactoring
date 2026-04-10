package com.example.emotionalapp.ui.alltraining

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.emotionalapp.data.TrainingItem
import com.example.emotionalapp.data.TrainingMenuItem
import com.example.emotionalapp.data.TrainingMenuType
import com.example.emotionalapp.data.TrainingType

@Composable
fun TrainingItemCard(
    item: TrainingMenuItem,
    onClick: () -> Unit
) {
    val backgroundColor =
        item.backgroundColorResId?.let { colorResource(id = it) } ?: Color(0xFFE91E63)

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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingItemCardPreview() {
    TrainingItemCard(
        item = TrainingMenuItem(
            id = "1",
            title = "대표 훈련 시작하기",
            subtitle = "가장 중요한 훈련을 바로 경험해보세요",
            type = TrainingMenuType.EMOTION, // 네 enum 값에 맞게 수정
            backgroundColorResId = R.color.button_color_emotion,
        ),
        onClick = {}
    )
}