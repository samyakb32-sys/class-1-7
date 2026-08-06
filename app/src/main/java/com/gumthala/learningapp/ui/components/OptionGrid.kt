package com.gumthala.learningapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

/**
 * `.grid2` / `.opt-grid` — a 2-column answer grid. Correct cells switch to the
 * green fill with a ✓ in the top-right, exactly as `.opt-cell.right::after` does.
 *
 * The list arrives pre-shuffled from QuizEngine; this composable never reorders
 * it, so the correct answer's position stays unpredictable.
 */
@Composable
fun OptionGrid(
    options: List<String>,
    correctIndex: Int?,
    selectedIndex: Int?,
    revealCorrect: Boolean,
    showLetters: Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val letters = listOf("A", "B", "C", "D", "E", "F")
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowOptions.forEachIndexed { columnIndex, label ->
                    val index = rowIndex * 2 + columnIndex
                    val isCorrect = revealCorrect && index == correctIndex
                    val isSelectedWrong =
                        revealCorrect && index == selectedIndex && index != correctIndex
                    OptionCell(
                        text = if (showLetters) "${letters.getOrElse(index) { "" }}. $label" else label,
                        isCorrect = isCorrect,
                        isSelectedWrong = isSelectedWrong,
                        onClick = { onSelect(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowOptions.size == 1) Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun OptionCell(
    text: String,
    isCorrect: Boolean,
    isSelectedWrong: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        isCorrect -> AppColors.GreenBg
        isSelectedWrong -> AppColors.PinkBg
        else -> AppColors.SurfaceSoft
    }
    val foreground = when {
        isCorrect -> Color(0xFF15803D)
        isSelectedWrong -> Color(0xFFD6316B)
        else -> AppColors.Ink
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.Cell))
            .background(background)
            .clickable(onClick = onClick)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = display(TextSize.Title, FontWeight.Bold), color = foreground)
        if (isCorrect) {
            Text(
                "✓",
                style = body(TextSize.Small, FontWeight.ExtraBold),
                color = foreground,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}
