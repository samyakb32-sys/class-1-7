package com.gumthala.learningapp.tv.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.Choice
import com.gumthala.learningapp.tv.content.Mood
import com.gumthala.learningapp.tv.content.Question
import com.gumthala.learningapp.tv.content.Words
import com.gumthala.learningapp.tv.ui.LocalEnv
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.components.ButtonKind
import com.gumthala.learningapp.tv.ui.components.CheckIcon
import com.gumthala.learningapp.tv.ui.components.CrossIcon
import com.gumthala.learningapp.tv.ui.components.FocusSurface
import com.gumthala.learningapp.tv.ui.components.PlayIcon
import com.gumthala.learningapp.tv.ui.components.Sunny
import com.gumthala.learningapp.tv.ui.components.TvButton
import com.gumthala.learningapp.tv.ui.components.paper
import com.gumthala.learningapp.tv.ui.components.rememberAutoFocus
import com.gumthala.learningapp.tv.ui.theme.Tv
import com.gumthala.learningapp.tv.ui.visuals.VisualView
import kotlinx.coroutines.delay

private val cheers = listOf("Great job!", "Yes! Well done!", "You got it!", "Super!", "Fantastic!", "Brilliant!", "Correct! Clever you!")

private enum class Tile { NORMAL, WRONG, RIGHT }

private sealed interface Banner {
    data class Hint(val text: String) : Banner
    data class Good(val text: String) : Banner
    data class Answer(val text: String) : Banner
}

/** Generous stars: finishing is always worth one, and there is no way to "fail". */
fun starsFor(firstTry: Int, total: Int): Int {
    if (total <= 0) return 1
    val pct = firstTry * 100 / total
    return when {
        pct >= 75 -> 3
        pct >= 50 -> 2
        else -> 1
    }
}

/**
 * One practice round. Wrong once: a gentle hint. Wrong twice (or "Show me how"): the worked answer
 * is animated for them. Nobody is ever left stuck, and nobody loses anything for trying.
 */
@Composable
fun PracticeScreen(
    title: String,
    questions: List<Question>,
    onExit: () -> Unit,
    onFinished: (firstTry: Int, total: Int) -> Unit,
    language: String = "en",
) {
    val env = LocalEnv.current
    var qi by remember(questions) { mutableStateOf(0) }
    var firstTry by remember(questions) { mutableStateOf(0) }
    var tried by remember(questions, qi) { mutableStateOf(setOf<Int>()) }
    var solved by remember(questions, qi) { mutableStateOf(false) }
    var peeked by remember(questions, qi) { mutableStateOf(false) }
    var showSolution by remember(questions, qi) { mutableStateOf(false) }
    var banner by remember(questions, qi) { mutableStateOf<Banner?>(null) }
    var mood by remember(questions, qi) { mutableStateOf(Mood.HAPPY) }
    val q = questions[qi]
    val lastQuestion = qi == questions.lastIndex

    LaunchedEffect(language) { env.narrator.language = language }
    DisposableEffect(Unit) { onDispose { env.narrator.stop() } }

    fun advance() {
        if (lastQuestion) onFinished(firstTry, questions.size) else qi += 1
    }

    // Read the question aloud when it appears.
    LaunchedEffect(questions, qi) {
        delay(350)
        env.speak(Words.speakable(q.prompt))
    }

    // After a right answer: cheer, then move on by itself (unless they needed the worked answer).
    LaunchedEffect(questions, qi, solved) {
        if (solved && !peeked) {
            env.speak(cheers[qi % cheers.size])
            delay(450)
            advance()
        }
    }

    fun pick(i: Int) {
        if (solved || i in tried) return
        if (i == q.answer) {
            solved = true
            if (tried.isEmpty() && !peeked) firstTry += 1
            env.correct()
            mood = Mood.CHEER
            banner = Banner.Good(cheers[qi % cheers.size])
        } else {
            tried = tried + i
            env.wrong()
            mood = Mood.OOPS
            if (tried.size == 1) {
                banner = Banner.Hint(q.hint)
            } else if (q.solution.isNotEmpty()) {
                peeked = true
                showSolution = true
            } else {
                peeked = true
                solved = true
                banner = Banner.Answer("The answer is ${q.choices[q.answer].text}")
            }
        }
    }

    val firstFocus = rememberAutoFocus(key = qi)
    val nextFocus = rememberAutoFocus(key = solved, enabled = solved && peeked)

    if (showSolution) {
        TeachPlayer(
            title = q.prompt, steps = q.solution, doneLabel = "Got it",
            onDone = {
                showSolution = false
                solved = true
                mood = Mood.HAPPY
                banner = Banner.Answer("The answer is ${q.choices[q.answer].text}")
            },
            onExit = {
                showSolution = false
                solved = true
                banner = Banner.Answer("The answer is ${q.choices[q.answer].text}")
            },
        )
        return
    }

    val hasPictureChoices = q.choices.any { it.visual != null }
    val tileH = if (hasPictureChoices) 132.dp else 98.dp
    val n = q.choices.size
    val tileW = minOf(236f, (864f - 18f * (n - 1)) / n).dp
    Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp)) {
        Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
            BackChip(onExit)
            Spacer(Modifier.width(16.dp))
            AutoText(title, Modifier.weight(1f), maxSize = 30.sp, color = Color.White, align = TextAlign.Start)
            StepDots(qi, questions.size)
        }
        Spacer(Modifier.height(8.dp))
        Column(Modifier.weight(1f).fillMaxWidth().paper().padding(horizontal = 18.dp, vertical = 10.dp)) {
            if (q.visual != null) {
                AutoText(q.prompt, Modifier.fillMaxWidth(), maxSize = 36.sp, minSize = 18.sp, color = Tv.Ink, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Box(Modifier.weight(1f).fillMaxWidth()) { VisualView(q.visual, q.visual.maxProgress, Modifier.fillMaxSize()) }
            } else {
                // A text-only question (the class question banks): the words are the whole picture.
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AutoText(q.prompt, Modifier.fillMaxWidth().padding(horizontal = 12.dp), maxSize = 52.sp, minSize = 20.sp, color = Tv.Ink, maxLines = 4)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterHorizontally)) {
            q.choices.forEachIndexed { i, c ->
                val state = when {
                    solved && i == q.answer -> Tile.RIGHT
                    i in tried -> Tile.WRONG
                    else -> Tile.NORMAL
                }
                AnswerTile(c, state, tileH, Modifier.width(tileW), if (i == 0) firstFocus else null) { pick(i) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().height(76.dp), verticalAlignment = Alignment.CenterVertically) {
            Sunny(mood, 70.dp)
            Spacer(Modifier.width(12.dp))
            Box(Modifier.weight(1f)) { BannerPill(banner) }
            Spacer(Modifier.width(12.dp))
            if (solved && peeked) {
                TvButton(
                    if (lastQuestion) "Finish" else "Next", { advance() }, kind = ButtonKind.PRIMARY, height = 60.dp, textSize = 26.sp,
                    focusRequester = nextFocus, trailing = { c -> PlayIcon(c, 24.dp) },
                )
            } else if (!solved && q.solution.isNotEmpty()) {
                TvButton("Show me how", { peeked = true; showSolution = true }, kind = ButtonKind.SOFT, height = 56.dp, textSize = 22.sp)
            }
        }
    }
}

@Composable
private fun AnswerTile(choice: Choice, state: Tile, height: Dp, modifier: Modifier, focusRequester: androidx.compose.ui.focus.FocusRequester?, onClick: () -> Unit) {
    val bg = when (state) { Tile.NORMAL -> Tv.Paper; Tile.WRONG -> Tv.RedSoft; Tile.RIGHT -> Tv.GreenSoft }
    val fg = when (state) { Tile.NORMAL -> Tv.Purple; Tile.WRONG -> Tv.Red; Tile.RIGHT -> Tv.GreenDark }
    FocusSurface(
        onClick = onClick, modifier = modifier.height(height), shape = RoundedCornerShape(26.dp),
        color = bg, focusedColor = if (state == Tile.NORMAL) Color.White else bg, focusRequester = focusRequester,
    ) {
        Box(Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
            if (choice.visual != null) {
                VisualView(choice.visual, choice.visual.maxProgress, Modifier.fillMaxSize())
            } else {
                AutoText(choice.text, Modifier.fillMaxWidth(), maxSize = 48.sp, minSize = 16.sp, color = fg, maxLines = 2)
            }
        }
        if (state == Tile.WRONG) Box(Modifier.align(Alignment.TopEnd).padding(8.dp)) { CrossIcon(Tv.Red, 30.dp) }
        if (state == Tile.RIGHT) Box(Modifier.align(Alignment.TopEnd).padding(8.dp)) { CheckIcon(Tv.GreenDark, 32.dp) }
    }
}

@Composable
private fun BannerPill(banner: Banner?) {
    val (bg, fg, text) = when (banner) {
        is Banner.Hint -> Triple(Tv.YellowSoft, Color(0xFF7A4D00), "Hint: ${banner.text}")
        is Banner.Good -> Triple(Tv.GreenSoft, Tv.GreenDark, banner.text)
        is Banner.Answer -> Triple(Tv.GreenSoft, Tv.GreenDark, banner.text)
        null -> return
    }
    Box(Modifier.fillMaxWidth().background(bg, RoundedCornerShape(22.dp)).padding(horizontal = 18.dp, vertical = 8.dp), contentAlignment = Alignment.CenterStart) {
        AutoText(text, Modifier.fillMaxWidth(), maxSize = 26.sp, minSize = 15.sp, color = fg, align = TextAlign.Start, maxLines = 2)
    }
}
