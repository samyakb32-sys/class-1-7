package com.gumthala.learningapp.ui.screens.roster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

/** A simple "which class?" step, shared by the question-editor flow for both Teacher and Admin. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PickClassScreen(
    title: String,
    allowedClassLevels: List<Int>,
    onBack: () -> Unit,
    onPick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", style = display(TextSize.Header, FontWeight.Bold), color = AppColors.Ink,
                modifier = Modifier.clickable(onClick = onBack))
            Text(
                title,
                style = display(TextSize.Header, FontWeight.ExtraBold),
                color = AppColors.Ink,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            androidx.compose.foundation.layout.Spacer(Modifier.size(20.dp))
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            allowedClassLevels.forEach { level ->
                Text(
                    "Class $level",
                    style = body(TextSize.Small, FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Radius.Card))
                        .background(AppColors.Purple)
                        .clickable { onPick(level) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
