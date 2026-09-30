package com.gumthala.learningapp.tv.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.Step
import com.gumthala.learningapp.tv.content.Words
import com.gumthala.learningapp.tv.content.spokenAt
import com.gumthala.learningapp.tv.ui.LocalEnv
import com.gumthala.learningapp.tv.ui.Env
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.components.ButtonKind
import com.gumthala.learningapp.tv.ui.components.PlayIcon
import com.gumthala.learningapp.tv.ui.components.ReplayIcon
import com.gumthala.learningapp.tv.ui.components.ChevronIcon
import com.gumthala.learningapp.tv.ui.components.Sunny
import com.gumthala.learningapp.tv.ui.components.TvButton
import com.gumthala.learningapp.tv.ui.components.paper
import com.gumthala.learningapp.tv.ui.components.rememberAutoFocus
import com.gumthala.learningapp.tv.ui.theme.Tv
import com.gumthala.learningapp.tv.ui.visuals.VisualView
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Plays one step: speaks the narration and runs the reveal animation, calling [onProgress] as things
 * appear. Counting steps say the narration first, then each number as it lights up. Everything else
 * animates alongside the narration. With the voice off, the same pacing is kept using timers.
 */
suspend fun runStep(step: Step, env: Env, onProgress: (Int) -> Unit) {
    val full = step.visual.maxProgress
    val max = minOf(step.to, full)
    val start = if (step.from >= max) max else step.from.coerceAtLeast(0)
    onProgress(start)
    val text = Words.speakable(step.say)
    val gap = when {
        max <= 6 -> 750L
        max <= 12 -> 540L
        else -> 400L
    }
    coroutineScope {
        if (step.counting) {
            env.speak(text)
            delay(250)
            for (p in start + 1..max) {
                onProgress(p)
                env.tick()
                val word = step.visual.spokenAt(p)
                if (word != null) env.speak(word) else delay(gap)
            }
        } else {
            val speech = launch { env.speak(text) }
            for (p in start + 1..max) {
                delay(gap)
                onProgress(p)
                env.tick()
            }
            speech.join()
        }
    }
}

/**
 * The teaching board: a picture that builds itself, a big caption that is also spoken, and three
 * big buttons (back a step, say it again, next). Used for lessons and for worked answers.
 */
@Composable
fun TeachPlayer(
    title: String,
    steps: List<Step>,
    doneLabel: String,
    onDone: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val env = LocalEnv.current
    var index by remember(steps) { mutableStateOf(0) }
    var replay by remember(steps) { mutableStateOf(0) }
    var progress by remember(steps) { mutableStateOf(0) }
    var ready by remember(steps) { mutableStateOf(false) }
    val step = steps[index]
    val last = index == steps.lastIndex

    // Lessons are always in English; an earlier Hindi or Marathi quiz must not leave the voice in that language.
    LaunchedEffect(Unit) { env.narrator.language = "en" }
    LaunchedEffect(steps, index, replay) {
        ready = false
        runStep(step, env) { progress = it }
        ready = true
    }
    // Leaving the screen (or jumping to another step) must silence the narrator at once.
    androidx.compose.runtime.DisposableEffect(steps, index, replay) {
        onDispose { env.narrator.stop() }
    }

    val nextFocus = rememberAutoFocus(key = index)

    Column(modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp)) {
        Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
            BackChip(onExit)
            Spacer(Modifier.width(16.dp))
            AutoText(title, Modifier.weight(1f), maxSize = 32.sp, color = Color.White, align = TextAlign.Start)
            StepDots(index, steps.size)
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.weight(1f).fillMaxWidth().paper().padding(14.dp)) {
            VisualView(step.visual, progress, Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().height(96.dp), verticalAlignment = Alignment.CenterVertically) {
            Sunny(step.mood, 92.dp)
            Spacer(Modifier.width(18.dp))
            AutoText(step.say, Modifier.weight(1f), maxSize = 30.sp, minSize = 18.sp, color = Color.White, align = TextAlign.Start, maxLines = 3)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            if (index > 0) {
                TvButton(
                    "Back", { index -= 1; progress = 0 }, height = 58.dp, textSize = 24.sp, kind = ButtonKind.SOFT,
                    leading = { c -> ChevronIcon(c, 28.dp) },
                )
                Spacer(Modifier.width(18.dp))
            }
            TvButton(
                "Again", { replay += 1 }, height = 58.dp, textSize = 24.sp, kind = ButtonKind.SOFT,
                leading = { c -> ReplayIcon(c, 26.dp) },
            )
            Spacer(Modifier.width(18.dp))
            PulsingNext(ready) {
                TvButton(
                    if (last) doneLabel else "Next",
                    { if (last) onDone() else { index += 1; progress = 0 } },
                    kind = ButtonKind.PRIMARY, height = 66.dp, textSize = 28.sp, focusRequester = nextFocus,
                    trailing = { c -> PlayIcon(c, 26.dp) },
                )
            }
        }
    }
}

/** Gentle pulse on the Next button once the step has finished, so a child knows it is their turn. */
@Composable
private fun PulsingNext(ready: Boolean, content: @Composable () -> Unit) {
    val t = rememberInfiniteTransition()
    val pulse by t.animateFloat(1f, 1.05f, infiniteRepeatable(tween(650), RepeatMode.Reverse))
    Box(Modifier.graphicsLayer { val s = if (ready) pulse else 1f; scaleX = s; scaleY = s }) { content() }
}

@Composable
fun StepDots(index: Int, total: Int) {
    if (total > 14) {
        AutoText("${index + 1} / $total", Modifier.width(90.dp), maxSize = 22.sp, color = Color.White)
        return
    }
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until total) {
            Box(
                Modifier.size(if (i == index) 16.dp else 11.dp)
                    .background(if (i == index) Tv.Sun else if (i < index) Color.White else Color(0x55FFFFFF), CircleShape),
            )
        }
    }
}
