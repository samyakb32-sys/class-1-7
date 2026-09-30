package com.gumthala.learningapp.tv.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.Mood
import com.gumthala.learningapp.tv.core.SeedSubject
import com.gumthala.learningapp.tv.core.Seeds
import com.gumthala.learningapp.tv.core.localized
import com.gumthala.learningapp.tv.core.toQuestions
import com.gumthala.learningapp.tv.ui.LocalEnv
import com.gumthala.learningapp.tv.ui.Route
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.components.ButtonKind
import com.gumthala.learningapp.tv.ui.components.FocusSurface
import com.gumthala.learningapp.tv.ui.components.StarBar
import com.gumthala.learningapp.tv.ui.components.Sunny
import com.gumthala.learningapp.tv.ui.components.TvButton
import com.gumthala.learningapp.tv.ui.components.rememberAutoFocus
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private class SubjectInfo(val code: String, val label: String, val emoji: String, val tint: Color)

private val subjects = listOf(
    SubjectInfo("maths", "Maths", "🧮", Tv.OrangeSoft),
    SubjectInfo("english", "English", "🔤", Tv.BlueSoft),
    SubjectInfo("marathi", "मराठी", "📖", Tv.PinkSoft),
    SubjectInfo("hindi", "हिंदी", "📝", Tv.GreenSoft),
)

private val languages = listOf("en" to "English", "mr" to "मराठी", "hi" to "हिंदी")

private fun languageName(code: String) = languages.firstOrNull { it.first == code }?.second ?: "English"

@Composable
fun SubjectsScreen() {
    val env = LocalEnv.current
    val nav = env.nav
    val focus = rememberAutoFocus()
    ScreenFrame("More Subjects  —  practice by class", onBack = { nav.pop() }) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(18.dp, Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                subjects.forEachIndexed { i, s ->
                    FocusSurface(
                        onClick = { nav.push(Route.SeedClass(s.code)) }, modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(30.dp), color = s.tint, focusedColor = Color.White, focusRequester = if (i == 0) focus else null,
                    ) {
                        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            BasicText(s.emoji, style = Tv.text(64.sp))
                            Spacer(Modifier.height(8.dp))
                            AutoText(s.label, Modifier.fillMaxWidth(), maxSize = 34.sp, color = Tv.Ink)
                        }
                    }
                }
            }
            BasicText("Questions are shown in:", style = Tv.text(24.sp, Color.White))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for ((code, name) in languages) {
                    TvButton(
                        name, { env.progress.changeSeedLanguage(code) },
                        kind = if (env.progress.seedLanguage == code) ButtonKind.PRIMARY else ButtonKind.SOFT, height = 58.dp, textSize = 24.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun SeedClassScreen(subject: String) {
    val env = LocalEnv.current
    val nav = env.nav
    val focus = rememberAutoFocus()
    val info = subjects.firstOrNull { it.code == subject }
    ScreenFrame("${info?.emoji ?: ""}  ${info?.label ?: subject}  —  which class?", onBack = { nav.pop() }) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(Modifier.fillMaxWidth().height(230.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                for (c in 1..7) {
                    FocusSurface(
                        onClick = { nav.push(Route.SeedChapters(subject, c)) }, modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(28.dp), color = Color.White, focusRequester = if (c == 1) focus else null,
                    ) {
                        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            BasicText("Class", style = Tv.text(20.sp, Tv.Muted))
                            BasicText("$c", style = Tv.text(84.sp, Tv.Purple, androidx.compose.ui.text.font.FontWeight.ExtraBold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberSubject(subject: String): SeedSubject? {
    val env = LocalEnv.current
    val state by produceState<SeedSubject?>(null, subject) {
        value = withContext(Dispatchers.Default) { Seeds.load(env.platform.assets, subject) }
    }
    return state
}

@Composable
private fun Loading() {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Sunny(Mood.THINK, 150.dp)
        BasicText("Getting the questions ready…", style = Tv.text(28.sp, Color.White))
    }
}

@Composable
fun SeedChaptersScreen(subject: String, cls: Int) {
    val env = LocalEnv.current
    val nav = env.nav
    val data = rememberSubject(subject)
    val lang = env.progress.seedLanguage
    val focus = rememberAutoFocus(key = data != null)
    val info = subjects.firstOrNull { it.code == subject }
    ScreenFrame("${info?.label ?: subject}  •  Class $cls", onBack = { nav.pop() }) {
        if (data == null) { Loading(); return@ScreenFrame }
        val chapters = data.classes[cls].orEmpty()
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            itemsIndexed(chapters) { i, ch ->
                FocusSurface(
                    onClick = { nav.push(Route.SeedQuiz(subject, cls, ch.id)) }, modifier = Modifier.fillMaxWidth().height(82.dp),
                    shape = RoundedCornerShape(26.dp), color = Color.White, focusRequester = if (i == 0) focus else null,
                ) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            AutoText(ch.title.localized(lang), Modifier.fillMaxWidth(), maxSize = 30.sp, color = Tv.Ink, align = TextAlign.Start)
                            BasicText("${ch.questions.size} questions  •  in ${languageName(lang)}", style = Tv.text(18.sp, Tv.Muted))
                        }
                        StarBar(env.progress.chapterStars(ch.id), 34.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun SeedQuizRoute(route: Route.SeedQuiz) {
    val env = LocalEnv.current
    val nav = env.nav
    val data = rememberSubject(route.subject)
    val lang = env.progress.seedLanguage
    if (data == null) { ScreenFrame("Getting ready", onBack = { nav.pop() }) { Loading() }; return }
    val chapter = data.classes[route.cls].orEmpty().firstOrNull { it.id == route.chapterId }
    if (chapter == null) { nav.pop(); return }
    val questions = remember(route) { chapter.toQuestions(lang) }
    PracticeScreen(
        title = chapter.title.localized(lang), questions = questions, language = lang,
        onExit = { nav.pop() },
        onFinished = { firstTry, total ->
            val stars = starsFor(firstTry, total)
            env.progress.recordChapter(chapter.id, stars)
            nav.replace(Route.Result(route.key, stars, firstTry, total))
        },
    )
}
