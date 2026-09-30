package com.gumthala.learningapp.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.Curriculum
import com.gumthala.learningapp.tv.content.Lesson
import com.gumthala.learningapp.tv.content.Mood
import com.gumthala.learningapp.tv.content.World
import com.gumthala.learningapp.tv.ui.LocalEnv
import com.gumthala.learningapp.tv.ui.Route
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.components.ButtonKind
import com.gumthala.learningapp.tv.ui.components.FocusSurface
import com.gumthala.learningapp.tv.ui.components.PlayIcon
import com.gumthala.learningapp.tv.ui.components.StarBar
import com.gumthala.learningapp.tv.ui.components.Sunny
import com.gumthala.learningapp.tv.ui.components.TvButton
import com.gumthala.learningapp.tv.ui.components.rememberAutoFocus
import com.gumthala.learningapp.tv.ui.theme.Tv

private var greeted = false

@Composable
fun HomeScreen() {
    val env = LocalEnv.current
    val nav = env.nav
    val progress = env.progress
    val next = progress.nextLesson()
    val world = Curriculum.worldOf(next.id)
    val focus = rememberAutoFocus()
    val firstTime = progress.totalStars == 0
    val greeting = if (firstTime) "Hi friend! I'm Sunny. Let's learn maths together!" else "Welcome back! Ready to learn?"

    LaunchedEffect(Unit) {
        if (!greeted) {
            greeted = true
            kotlinx.coroutines.delay(700)
            env.speak(greeting)
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 24.dp)) {
        Row(Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically) {
            AutoText("Maths Adventure", Modifier.weight(1f), maxSize = 40.sp, color = Tv.Sun, align = TextAlign.Start)
            StarsChip(progress.totalStars)
            Spacer(Modifier.width(14.dp))
            FocusSurface(onClick = { nav.push(Route.Settings) }, modifier = Modifier.size(52.dp), shape = CircleShape, color = Color(0x33FFFFFF), focusedColor = Color.White) {
                Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) { BasicText("⚙", style = Tv.text(30.sp, Color.White)) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.weight(1f).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.width(250.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Sunny(Mood.CHEER, 210.dp)
                Spacer(Modifier.height(6.dp))
                Box(Modifier.background(Color.White, RoundedCornerShape(22.dp)).padding(horizontal = 16.dp, vertical = 10.dp)) {
                    AutoText(greeting, Modifier.fillMaxWidth(), maxSize = 21.sp, minSize = 14.sp, color = Tv.Ink, maxLines = 3)
                }
            }
            Spacer(Modifier.width(26.dp))
            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)) {
                // The one big button: pick up exactly where you left off.
                FocusSurface(
                    onClick = {
                        nav.push(if (progress.starsOf(next.id) == 0) Route.Learn(next.id) else Route.Hub(next.id))
                    },
                    modifier = Modifier.fillMaxWidth().height(132.dp), shape = RoundedCornerShape(32.dp),
                    color = Color.White, focusRequester = focus,
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                        BasicText(next.icon, style = Tv.text(68.sp))
                        Spacer(Modifier.width(18.dp))
                        Column(Modifier.weight(1f)) {
                            BasicText(if (firstTime) "START HERE" else "CONTINUE", style = Tv.text(20.sp, Tv.Muted))
                            AutoText(next.title, Modifier.fillMaxWidth(), maxSize = 38.sp, color = Tv.Ink, align = TextAlign.Start)
                            if (world != null) AutoText("${world.emoji}  ${world.title}", Modifier.fillMaxWidth(), maxSize = 20.sp, color = Tv.Purple, align = TextAlign.Start)
                        }
                        Box(Modifier.size(78.dp).background(Tv.Sun, CircleShape), contentAlignment = Alignment.Center) { PlayIcon(Tv.Ink, 40.dp) }
                    }
                }
                Row(Modifier.fillMaxWidth().height(150.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    HomeCard("🗺️", "Maths Map", Tv.BlueSoft, Modifier.weight(1f)) { nav.push(Route.Map) }
                    HomeCard("🎯", "Quick Practice", Tv.PinkSoft, Modifier.weight(1f)) { nav.push(Route.Mix) }
                    HomeCard("🏆", "My Stars", Tv.YellowSoft, Modifier.weight(1f)) { nav.push(Route.Stars) }
                    HomeCard("📚", "More Subjects", Tv.VioletSoft, Modifier.weight(1f)) { nav.push(Route.Subjects) }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        com.gumthala.learningapp.tv.ui.components.KeyHints(*defaultHints.toTypedArray(), modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun HomeCard(emoji: String, label: String, tint: Color, modifier: Modifier, onClick: () -> Unit) {
    FocusSurface(onClick = onClick, modifier = modifier.fillMaxHeight(), shape = RoundedCornerShape(28.dp), color = tint, focusedColor = Color.White) {
        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            BasicText(emoji, style = Tv.text(54.sp))
            Spacer(Modifier.height(6.dp))
            AutoText(label, Modifier.fillMaxWidth(), maxSize = 24.sp, minSize = 14.sp, color = Tv.Ink, maxLines = 2)
        }
    }
}

// ------------------------------------------------------------------------------------------------

@Composable
fun MapScreen() {
    val env = LocalEnv.current
    val nav = env.nav
    val next = env.progress.nextLesson()
    val worlds = Curriculum.worlds
    val recommended = worlds.indexOfFirst { w -> w.lessons.any { it.id == next.id } }.coerceAtLeast(0)
    val start = nav.focusMemory["map"] ?: recommended
    val state = rememberLazyListState(initialFirstVisibleItemIndex = (start - 1).coerceAtLeast(0))
    val focus = rememberAutoFocus()

    ScreenFrame("Maths Map  —  choose a world", onBack = { nav.pop() }, trailing = { StarsChip(env.progress.totalStars) }) {
        LazyRow(
            state = state, modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(worlds) { i, w ->
                val done = w.lessons.count { env.progress.starsOf(it.id) > 0 }
                WorldCard(
                    w, done, isNext = i == recommended,
                    focusRequester = if (i == start) focus else null,
                    onFocused = { nav.focusMemory["map"] = i },
                ) { nav.push(Route.World(w.id)) }
            }
        }
    }
}

@Composable
private fun WorldCard(w: World, done: Int, isNext: Boolean, focusRequester: androidx.compose.ui.focus.FocusRequester?, onFocused: () -> Unit, onClick: () -> Unit) {
    FocusSurface(
        onClick = onClick, modifier = Modifier.width(236.dp).height(318.dp), shape = RoundedCornerShape(30.dp),
        color = Color.White, focusRequester = focusRequester, onFocused = onFocused,
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxWidth().height(128.dp).background(Brush.verticalGradient(listOf(Color(w.color), Color(w.color).copy(alpha = 0.72f)))),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(w.emoji, style = Tv.text(84.sp))
                if (isNext) Box(Modifier.align(Alignment.TopEnd).padding(10.dp).background(Tv.Sun, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 3.dp)) {
                    BasicText("NEXT", style = Tv.text(16.sp, Tv.Ink))
                }
            }
            Column(Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BasicText(w.level.uppercase(), style = Tv.text(14.sp, Color(w.color)))
                AutoText(w.title, Modifier.fillMaxWidth(), maxSize = 27.sp, minSize = 16.sp, color = Tv.Ink, align = TextAlign.Start, maxLines = 2)
                AutoText(w.tagline, Modifier.fillMaxWidth(), maxSize = 17.sp, minSize = 12.sp, color = Tv.Muted, align = TextAlign.Start, maxLines = 2, weight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                ProgressBar(done.toFloat() / w.lessons.size, Color(w.color), Modifier.fillMaxWidth(), 10.dp)
                BasicText("$done of ${w.lessons.size} lessons", style = Tv.text(16.sp, Tv.Muted))
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------

@Composable
fun WorldScreen(worldId: String) {
    val env = LocalEnv.current
    val nav = env.nav
    val world = Curriculum.world(worldId) ?: run { nav.pop(); return }
    val next = env.progress.nextLesson()
    val recommended = world.lessons.indexOfFirst { it.id == next.id }
    val start = (nav.focusMemory["world:$worldId"] ?: recommended).coerceAtLeast(0)
    val cols = 4
    val state = rememberLazyGridState(initialFirstVisibleItemIndex = ((start / cols) - 1).coerceAtLeast(0) * cols)
    val focus = rememberAutoFocus()

    ScreenFrame("${world.emoji}  ${world.title}", onBack = { nav.pop() }, trailing = { StarsChip(env.progress.totalStars) }) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(cols), state = state, modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            itemsIndexed(world.lessons) { i, l ->
                LessonCard(
                    i + 1, l, env.progress.starsOf(l.id), isNext = i == recommended, color = Color(world.color),
                    focusRequester = if (i == start) focus else null,
                    onFocused = { nav.focusMemory["world:$worldId"] = i },
                ) { nav.push(Route.Hub(l.id)) }
            }
        }
    }
}

@Composable
private fun LessonCard(number: Int, l: Lesson, stars: Int, isNext: Boolean, color: Color, focusRequester: androidx.compose.ui.focus.FocusRequester?, onFocused: () -> Unit, onClick: () -> Unit) {
    FocusSurface(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(172.dp), shape = RoundedCornerShape(24.dp),
        color = Color.White, focusRequester = focusRequester, onFocused = onFocused,
    ) {
        Column(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().height(46.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(28.dp).background(color, CircleShape), contentAlignment = Alignment.Center) { BasicText("$number", style = Tv.text(16.sp, Color.White)) }
                Spacer(Modifier.weight(1f))
                BasicText(l.icon, style = Tv.text(38.sp))
                Spacer(Modifier.weight(1f))
                Box(Modifier.width(56.dp), contentAlignment = Alignment.CenterEnd) {
                    if (isNext) Box(Modifier.background(Tv.Sun, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp)) { BasicText("NEXT", style = Tv.text(13.sp, Tv.Ink)) }
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                AutoText(l.title, Modifier.fillMaxWidth(), maxSize = 21.sp, minSize = 12.sp, color = Tv.Ink, maxLines = 2)
            }
            StarBar(stars, 22.dp)
        }
    }
}

// ------------------------------------------------------------------------------------------------

@Composable
fun HubScreen(lessonId: String) {
    val env = LocalEnv.current
    val nav = env.nav
    val lesson = Curriculum.lesson(lessonId) ?: run { nav.pop(); return }
    val world = Curriculum.worldOf(lessonId)
    val stars = env.progress.starsOf(lessonId)
    val focus = rememberAutoFocus()

    ScreenFrame(world?.let { "${it.emoji}  ${it.title}" } ?: "Lesson", onBack = { nav.pop() }, trailing = { StarsChip(env.progress.totalStars) }) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                Modifier.width(760.dp).background(Color.White, RoundedCornerShape(34.dp)).padding(horizontal = 36.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicText(lesson.icon, style = Tv.text(84.sp))
                    Spacer(Modifier.width(24.dp))
                    Column(Modifier.weight(1f)) {
                        AutoText(lesson.title, Modifier.fillMaxWidth(), maxSize = 46.sp, minSize = 24.sp, color = Tv.Ink, align = TextAlign.Start, maxLines = 2)
                        AutoText(lesson.blurb, Modifier.fillMaxWidth(), maxSize = 26.sp, minSize = 16.sp, color = Tv.Muted, align = TextAlign.Start, maxLines = 2, weight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    }
                    StarBar(stars, 46.dp)
                }
                Spacer(Modifier.height(26.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                    TvButton(
                        "Watch & learn", { nav.push(Route.Learn(lessonId)) }, kind = if (stars == 0) ButtonKind.PRIMARY else ButtonKind.SOFT,
                        height = 78.dp, textSize = 30.sp, emoji = "👀", focusRequester = if (stars == 0) focus else null,
                    )
                    TvButton(
                        "Practice", { nav.push(Route.Practice(lessonId)) }, kind = if (stars == 0) ButtonKind.SOFT else ButtonKind.PRIMARY,
                        height = 78.dp, textSize = 30.sp, emoji = "✏️", focusRequester = if (stars == 0) null else focus,
                    )
                }
            }
        }
    }
}
