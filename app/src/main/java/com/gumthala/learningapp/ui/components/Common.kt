package com.gumthala.learningapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

/**
 * A soft drop shadow matching the app's rounded-card language — one call
 * instead of repeating `.shadow(elevation, shape, ...)` at every card site.
 * ponytail: shared modifier > per-screen shadow tuning.
 */
fun Modifier.softCard(shape: Shape, elevation: Dp = 3.dp): Modifier =
    this.shadow(elevation, shape, clip = false)

/** `.sec-head` — a bold heading with an optional trailing purple action. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = display(TextSize.Label, FontWeight.Bold), color = AppColors.Ink)
        if (action != null) {
            Text(
                action,
                style = body(TextSize.Small, FontWeight.Bold),
                color = AppColors.Purple,
                modifier = Modifier.then(
                    if (onActionClick != null) Modifier.clickable(onClick = onActionClick) else Modifier
                )
            )
        }
    }
}

/** `.avatar` / `.feat .ic` / `.subj-card .ic` — a rounded tile holding an emoji glyph. */
@Composable
fun EmojiTile(
    emoji: String,
    background: Color,
    size: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = TextSize.Screen
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, style = body(fontSize, FontWeight.Bold))
    }
}

/** `.full-btn` — full-width purple primary action, with a soft shadow and a subtle press-in. */
@Composable
fun PrimaryFullButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "buttonPress"
    )
    val shape = RoundedCornerShape(Radius.Card)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .then(if (enabled) Modifier.softCard(shape, 4.dp) else Modifier)
            .clip(shape)
            .background(if (enabled) AppColors.Purple else AppColors.Muted)
            .clickable(interactionSource = interactionSource, indication = null, enabled = enabled, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = display(TextSize.Label, FontWeight.Bold), color = Color.White)
    }
}

/** The purple → light-purple gradient used by the quiz and progress headers. */
val PurpleGradient = Brush.linearGradient(listOf(AppColors.Purple, AppColors.PurpleLight))

/** The 135° purple → dark-purple gradient used by the home banner. */
val PurpleDeepGradient = Brush.linearGradient(listOf(AppColors.Purple, AppColors.PurpleDark))

/**
 * `.lesson-head` / `.quiz-head` / `.prog-head` — the coloured bar that bleeds to
 * both screen edges (`margin:0 -14px` in the mockup). Screens therefore apply
 * their horizontal padding to the scrolling body, never to the root column.
 */
@Composable
fun BleedHeader(
    modifier: Modifier = Modifier,
    gradient: Brush? = null,
    solid: Color? = null,
    contentPadding: androidx.compose.foundation.layout.PaddingValues =
        androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                when {
                    gradient != null -> Modifier.background(gradient)
                    solid != null -> Modifier.background(solid)
                    else -> Modifier
                }
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        content = content
    )
}

/** Vertical stack helper matching the mockup's tight label/value pairs. */
@Composable
fun LabelledValue(
    value: String,
    label: String,
    valueColor: Color = AppColors.Ink,
    labelColor: Color = AppColors.Muted,
    align: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    Column(horizontalAlignment = align) {
        Text(value, style = display(TextSize.Title, FontWeight.ExtraBold), color = valueColor)
        Text(label, style = body(TextSize.Micro, FontWeight.Bold), color = labelColor)
    }
}
