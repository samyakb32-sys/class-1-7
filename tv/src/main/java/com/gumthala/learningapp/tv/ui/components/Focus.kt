package com.gumthala.learningapp.tv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.withFrameNanos
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlinx.coroutines.delay

/**
 * Something you can point at with the remote. When it has focus it grows, gets a bright ring and a
 * deeper shadow, so a child can always see where they are. OK / Enter / touch / mouse all click it.
 */
@Composable
fun FocusSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    color: Color = Tv.Paper,
    focusedColor: Color = color,
    focusRequester: FocusRequester? = null,
    onFocused: () -> Unit = {},
    enabled: Boolean = true,
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.07f else 1f, spring(stiffness = 500f), label = "focusScale")
    val source = remember { MutableInteractionSource() }
    var m = modifier
        .zIndex(if (focused) 1f else 0f)
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .shadow(if (focused) 18.dp else 5.dp, shape)
        .clip(shape)
        .background(if (focused) focusedColor else color)
        .border(if (focused) 6.dp else 0.dp, if (focused) Tv.FocusRing else Color.Transparent, shape)
        .onFocusChanged {
            focused = it.isFocused
            if (it.isFocused) onFocused()
        }
    if (focusRequester != null) m = m.focusRequester(focusRequester)
    m = m.clickable(interactionSource = source, indication = null, enabled = enabled, onClick = onClick)
    Box(m) { content(focused) }
}

enum class ButtonKind { PRIMARY, SECONDARY, SOFT, DANGER }

/** A big friendly button: optional emoji + label. */
@Composable
fun TvButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String? = null,
    kind: ButtonKind = ButtonKind.SECONDARY,
    textSize: TextUnit = 26.sp,
    height: Dp = 64.dp,
    focusRequester: FocusRequester? = null,
    onFocused: () -> Unit = {},
    enabled: Boolean = true,
    leading: (@Composable (Color) -> Unit)? = null,
    trailing: (@Composable (Color) -> Unit)? = null,
) {
    val (bg, fg) = when (kind) {
        ButtonKind.PRIMARY -> Tv.Sun to Tv.Ink
        ButtonKind.SECONDARY -> Tv.Paper to Tv.Purple
        ButtonKind.SOFT -> Tv.PaperSoft to Tv.Purple
        ButtonKind.DANGER -> Tv.RedSoft to Tv.Red
    }
    FocusSurface(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minWidth = 150.dp).height(height),
        shape = RoundedCornerShape(height / 2),
        color = bg,
        focusedColor = if (kind == ButtonKind.PRIMARY) Color(0xFFFFD96B) else Color.White,
        focusRequester = focusRequester,
        onFocused = onFocused,
        enabled = enabled,
    ) {
        Row(
            Modifier.align(Alignment.Center).padding(horizontal = 26.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (leading != null) leading(fg)
            if (emoji != null) BasicText(emoji, style = Tv.text(textSize, fg))
            BasicText(label, style = Tv.text(textSize, fg), maxLines = 1)
            if (trailing != null) trailing(fg)
        }
    }
}

/**
 * Text that shrinks to fit instead of being cut off. Long Hindi/Marathi words and big numbers both
 * need this; a fixed size is always too big for something.
 */
@Composable
fun AutoText(
    text: String,
    modifier: Modifier = Modifier,
    maxSize: TextUnit = 28.sp,
    minSize: TextUnit = 13.sp,
    color: Color = Tv.Ink,
    weight: FontWeight = FontWeight.Bold,
    align: TextAlign = TextAlign.Center,
    maxLines: Int = 1,
    family: FontFamily = FontFamily.Default,
    softWrap: Boolean = maxLines > 1,
) {
    var size by remember(text, maxSize) { mutableStateOf(maxSize.value) }
    BasicText(
        text = text,
        modifier = modifier,
        style = Tv.text(size.sp, color, weight, family).copy(textAlign = align),
        maxLines = maxLines,
        softWrap = softWrap,
        overflow = TextOverflow.Clip,
        onTextLayout = { r ->
            if ((r.didOverflowWidth || r.didOverflowHeight) && size > minSize.value) {
                size = maxOf(minSize.value, size * 0.9f)
            }
        },
    )
}

/**
 * A FocusRequester that grabs focus when this screen (or [key]) appears, so the remote works the
 * instant a screen opens. Without this a TV shows nothing highlighted until a key is pressed.
 */
@Composable
fun rememberAutoFocus(key: Any? = Unit, enabled: Boolean = true): FocusRequester {
    val requester = remember { FocusRequester() }
    LaunchedEffect(key, enabled) {
        if (enabled) {
            // The node is attached a frame or two after composition; the window itself can take a
            // moment to gain focus at app start. Ask twice; re-asking an already-focused item is harmless.
            withFrameNanos { }
            withFrameNanos { }
            try { requester.requestFocus() } catch (_: Exception) { }
            delay(250)
            try { requester.requestFocus() } catch (_: Exception) { }
        }
    }
    return requester
}
