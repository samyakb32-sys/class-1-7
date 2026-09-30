package com.gumthala.learningapp.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.components.ChevronIcon
import com.gumthala.learningapp.tv.ui.components.FocusSurface
import com.gumthala.learningapp.tv.ui.components.KeyHints
import com.gumthala.learningapp.tv.ui.components.StarIcon
import com.gumthala.learningapp.tv.ui.theme.Tv

val defaultHints = listOf("← → ↑ ↓" to "Move", "OK" to "Choose", "BACK" to "Go back")

/** Title bar, content area and the key hints; every menu screen uses it. */
@Composable
fun ScreenFrame(
    title: String,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    hints: List<Pair<String, String>> = defaultHints,
    trailing: (@Composable RowScope.() -> Unit)? = null,
    backFocus: FocusRequester? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp)) {
        Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                BackChip(onBack, backFocus)
                Spacer(Modifier.width(16.dp))
            }
            AutoText(title, Modifier.weight(1f), maxSize = 38.sp, color = Color.White, align = TextAlign.Start)
            if (trailing != null) trailing()
        }
        Spacer(Modifier.height(10.dp))
        Column(Modifier.weight(1f).fillMaxWidth(), content = content)
        Spacer(Modifier.height(6.dp))
        KeyHints(*hints.toTypedArray(), modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun BackChip(onClick: () -> Unit, focusRequester: FocusRequester? = null) {
    FocusSurface(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = Color(0x33FFFFFF),
        focusedColor = Color.White,
        focusRequester = focusRequester,
    ) { focused ->
        Box(Modifier.size(56.dp), contentAlignment = Alignment.Center) {
            ChevronIcon(if (focused) Tv.Purple else Color.White, 34.dp)
        }
    }
}

@Composable
fun StarsChip(total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier.background(Color(0x33FFFFFF), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StarIcon(true, 32.dp)
        BasicText("$total", style = Tv.text(28.sp, Color.White))
    }
}

/** A progress bar: a soft track with a coloured fill. */
@Composable
fun ProgressBar(fraction: Float, color: Color, modifier: Modifier = Modifier, height: androidx.compose.ui.unit.Dp = 10.dp) {
    Box(modifier.height(height).background(Color(0x22000000), RoundedCornerShape(50))) {
        Box(Modifier.fillMaxWidth(fraction.coerceIn(0f, 1f)).height(height).background(color, RoundedCornerShape(50)))
    }
}
