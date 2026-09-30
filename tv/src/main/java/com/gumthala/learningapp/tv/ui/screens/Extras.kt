package com.gumthala.learningapp.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.Curriculum
import com.gumthala.learningapp.tv.content.Mood
import com.gumthala.learningapp.tv.ui.LocalEnv
import com.gumthala.learningapp.tv.ui.Route
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.components.ButtonKind
import com.gumthala.learningapp.tv.ui.components.Confetti
import com.gumthala.learningapp.tv.ui.components.FocusSurface
import com.gumthala.learningapp.tv.ui.components.PlayIcon
import com.gumthala.learningapp.tv.ui.components.StarBar
import com.gumthala.learningapp.tv.ui.components.StarIcon
import com.gumthala.learningapp.tv.ui.components.Sunny
import com.gumthala.learningapp.tv.ui.components.TvButton
import com.gumthala.learningapp.tv.ui.components.rememberAutoFocus
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(r: Route.Result) {
    val env = LocalEnv.current
    val nav = env.nav
    val lesson = if (r.retry.startsWith("practice:")) Curriculum.lesson(r.retry.removePrefix("practice:")) else null
    val nextLesson = lesson?.let { Curriculum.lessons.getOrNull(Curriculum.lessons.indexOf(it) + 1) }
    val weak = r.stars == 1 && r.correct * 2 < r.total
    val focus = rememberAutoFocus()
    val headline = when (r.stars) {
        3 -> "Amazing!"
        2 -> "Great job!"
        else -> "Good try!"
    }
    val spoken = when (r.stars) {
        3 -> "Amazing! You earned three stars!"
        2 -> "Great job! You earned two stars!"
        else -> "Good try! Let's practise a little more."
    }
    LaunchedEffect(Unit) {
        env.fanfare()
        delay(500)
        env.speak(spoken)
    }

    Box(Modifier.fillMaxSize()) {
        if (r.stars >= 2) Confetti()
        Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Sunny(if (r.stars >= 2) Mood.CHEER else Mood.HAPPY, 170.dp)
            BasicText(headline, style = Tv.text(58.sp, Tv.Sun, FontWeight.ExtraBold))
            Spacer(Modifier.height(6.dp))
            StarBar(r.stars, 92.dp, popIn = true)
            Spacer(Modifier.height(10.dp))
            BasicText("You got ${r.correct} of ${r.total} right the first time", style = Tv.text(26.sp, Color.White))
            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                if (lesson != null) {
                    if (weak) {
                        TvButton("Watch again", { nav.replace(Route.Learn(lesson.id)) }, kind = ButtonKind.PRIMARY, emoji = "👀", height = 70.dp, textSize = 28.sp, focusRequester = focus)
                        TvButton("Try again", { nav.replace(Route.parse(r.retry) ?: Route.Home) }, kind = ButtonKind.SECONDARY, emoji = "✏️", height = 62.dp, textSize = 24.sp)
                    } else {
                        if (nextLesson != null) {
                            TvButton(
                                "Next lesson",
                                { nav.replace(if (env.progress.starsOf(nextLesson.id) == 0) Route.Learn(nextLesson.id) else Route.Hub(nextLesson.id)) },
                                kind = ButtonKind.PRIMARY, height = 70.dp, textSize = 28.sp, focusRequester = focus,
                                trailing = { c -> PlayIcon(c, 26.dp) },
                            )
                        } else {
                            TvButton("All done!", { nav.home() }, kind = ButtonKind.PRIMARY, height = 70.dp, textSize = 28.sp, focusRequester = focus)
                        }
                        TvButton("Try again", { nav.replace(Route.parse(r.retry) ?: Route.Home) }, emoji = "✏️", height = 62.dp, textSize = 24.sp)
                    }
                    TvButton("Map", { nav.home(); nav.push(Route.Map) }, emoji = "🗺️", kind = ButtonKind.SOFT, height = 62.dp, textSize = 24.sp)
                } else {
                    TvButton("Play again", { nav.replace(Route.parse(r.retry) ?: Route.Home) }, kind = ButtonKind.PRIMARY, height = 70.dp, textSize = 28.sp, focusRequester = focus)
                    TvButton("Home", { nav.home() }, emoji = "🏠", kind = ButtonKind.SOFT, height = 62.dp, textSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun StarsScreen() {
    val env = LocalEnv.current
    val nav = env.nav
    val focus = rememberAutoFocus()
    val p = env.progress
    ScreenFrame("My Stars", onBack = { nav.pop() }, trailing = { StarsChip(p.totalStars) }) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            itemsIndexed(Curriculum.worlds) { i, w ->
                val got = w.lessons.sumOf { p.starsOf(it.id) }
                val max = w.lessons.size * 3
                val complete = w.lessons.all { p.starsOf(it.id) > 0 }
                FocusSurface(
                    onClick = { nav.push(Route.World(w.id)) }, modifier = Modifier.fillMaxWidth().height(86.dp),
                    shape = RoundedCornerShape(26.dp), color = Color.White, focusRequester = if (i == 0) focus else null,
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(58.dp).background(Color(w.color), CircleShape), contentAlignment = Alignment.Center) { BasicText(w.emoji, style = Tv.text(32.sp)) }
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f)) {
                            AutoText(w.title, Modifier.fillMaxWidth(), maxSize = 28.sp, color = Tv.Ink, align = TextAlign.Start)
                            ProgressBar(got.toFloat() / max, Color(w.color), Modifier.fillMaxWidth(0.9f), 10.dp)
                        }
                        Spacer(Modifier.width(14.dp))
                        if (complete) BasicText("🏅", style = Tv.text(38.sp))
                        Spacer(Modifier.width(10.dp))
                        StarIcon(true, 34.dp)
                        Spacer(Modifier.width(6.dp))
                        BasicText("$got / $max", style = Tv.text(26.sp, Tv.Ink))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val env = LocalEnv.current
    val nav = env.nav
    val p = env.progress
    var confirm by remember { mutableStateOf(false) }
    val focus = rememberAutoFocus(key = confirm)

    if (confirm) {
        ScreenFrame("Start again?", onBack = { confirm = false }) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Sunny(Mood.THINK, 150.dp)
                Spacer(Modifier.height(10.dp))
                BasicText("This will erase all the stars.", style = Tv.text(34.sp, Color.White))
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                    TvButton("Keep my stars", { confirm = false }, kind = ButtonKind.PRIMARY, height = 72.dp, textSize = 28.sp, focusRequester = focus)
                    TvButton("Erase", { p.reset(); confirm = false; nav.home() }, kind = ButtonKind.DANGER, height = 64.dp, textSize = 24.sp)
                }
            }
        }
        return
    }

    ScreenFrame("Settings", onBack = { nav.pop() }) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically)) {
            ToggleRow("Voice reads the lessons", if (env.narrator.available) "Sunny speaks every step" else "No voice found on this TV: captions still show", p.voice, focus) { p.changeVoice(it) }
            ToggleRow("Slow, clear voice", "Easier for little ears", p.slowVoice, null) { p.changeSlowVoice(it) }
            ToggleRow("Sound effects", "Pops, dings and cheers", p.effects, null) { p.changeEffects(it) }
            TvButton("Erase all stars", { confirm = true }, kind = ButtonKind.DANGER, height = 56.dp, textSize = 22.sp)
            BasicText("Maths Adventure TV  •  works without internet  •  no ads", style = Tv.text(18.sp, Color(0xAAFFFFFF)))
        }
    }
}

@Composable
private fun ToggleRow(label: String, sub: String, on: Boolean, focus: androidx.compose.ui.focus.FocusRequester?, onChange: (Boolean) -> Unit) {
    FocusSurface(
        onClick = { onChange(!on) }, modifier = Modifier.width(760.dp).height(84.dp),
        shape = RoundedCornerShape(28.dp), color = Color.White, focusRequester = focus,
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 26.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                AutoText(label, Modifier.fillMaxWidth(), maxSize = 30.sp, color = Tv.Ink, align = TextAlign.Start)
                AutoText(sub, Modifier.fillMaxWidth(), maxSize = 19.sp, color = Tv.Muted, align = TextAlign.Start, weight = FontWeight.SemiBold)
            }
            Box(Modifier.size(width = 96.dp, height = 50.dp).background(if (on) Tv.Green else Color(0xFFC9C6E2), RoundedCornerShape(50))) {
                Box(Modifier.padding(5.dp).size(40.dp).align(if (on) Alignment.CenterEnd else Alignment.CenterStart).background(Color.White, CircleShape))
            }
            Spacer(Modifier.width(14.dp))
            Box(Modifier.width(52.dp), contentAlignment = Alignment.CenterStart) { BasicText(if (on) "ON" else "OFF", style = Tv.text(22.sp, if (on) Tv.GreenDark else Tv.Muted)) }
        }
    }
}
