package com.gumthala.learningapp.tv.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.gumthala.learningapp.tv.content.Curriculum
import com.gumthala.learningapp.tv.core.Platform
import com.gumthala.learningapp.tv.platform.BackPress
import com.gumthala.learningapp.tv.ui.components.tvBackground
import com.gumthala.learningapp.tv.ui.screens.HomeScreen
import com.gumthala.learningapp.tv.ui.screens.HubScreen
import com.gumthala.learningapp.tv.ui.screens.MapScreen
import com.gumthala.learningapp.tv.ui.screens.PracticeScreen
import com.gumthala.learningapp.tv.ui.screens.ResultScreen
import com.gumthala.learningapp.tv.ui.screens.SeedChaptersScreen
import com.gumthala.learningapp.tv.ui.screens.SeedClassScreen
import com.gumthala.learningapp.tv.ui.screens.SeedQuizRoute
import com.gumthala.learningapp.tv.ui.screens.SettingsScreen
import com.gumthala.learningapp.tv.ui.screens.StarsScreen
import com.gumthala.learningapp.tv.ui.screens.SubjectsScreen
import com.gumthala.learningapp.tv.ui.screens.TeachPlayer
import com.gumthala.learningapp.tv.ui.screens.WorldScreen
import com.gumthala.learningapp.tv.ui.screens.starsFor
import com.gumthala.learningapp.tv.ui.theme.TvScaled
import kotlin.random.Random

/**
 * The whole app. There is no login and no account: it opens straight onto Home, and the remote's
 * BACK key simply goes back one screen (and, on Home, leaves the app like any other TV app).
 */
@Composable
fun TvApp(platform: Platform, initial: Navigator? = null) {
    val progress = remember { Progress(platform.store) }
    val nav = initial ?: rememberSaveable(saver = Navigator.Saver) { Navigator() }
    val env = remember(platform, progress, nav) { Env(platform, progress, nav) }

    CompositionLocalProvider(LocalEnv provides env) {
        TvScaled {
            Box(Modifier.fillMaxSize().tvBackground()) {
                BackPress(enabled = nav.stack.size > 1) { nav.pop() }
                Crossfade(targetState = nav.current, animationSpec = tween(200), label = "screen") { route ->
                    Screen(route)
                }
            }
        }
    }
}

@Composable
private fun Screen(route: Route) {
    val env = LocalEnv.current
    val nav = env.nav
    when (route) {
        Route.Home -> HomeScreen()
        Route.Map -> MapScreen()
        is Route.World -> WorldScreen(route.id)
        is Route.Hub -> HubScreen(route.lessonId)
        is Route.Learn -> {
            val lesson = Curriculum.lesson(route.lessonId)
            if (lesson == null) nav.pop() else TeachPlayer(
                title = lesson.title, steps = lesson.teach, doneLabel = "Practice",
                onDone = { nav.replace(Route.Practice(lesson.id)) }, onExit = { nav.pop() },
            )
        }
        is Route.Practice -> {
            val lesson = Curriculum.lesson(route.lessonId)
            if (lesson == null) nav.pop() else {
                val round = remember(route) { lesson.buildRound(Random.Default) }
                PracticeScreen(
                    title = lesson.title, questions = round, onExit = { nav.pop() },
                    onFinished = { firstTry, total ->
                        val stars = starsFor(firstTry, total)
                        env.progress.record(lesson.id, stars)
                        nav.replace(Route.Result(route.key, stars, firstTry, total))
                    },
                )
            }
        }
        Route.Mix -> {
            val round = remember(route) {
                val done = Curriculum.lessons.filter { env.progress.starsOf(it.id) > 0 }
                val pool = if (done.size >= 3) done else Curriculum.lessons.take(8)
                val rnd = Random.Default
                List(10) { pool.random(rnd).quiz(rnd, rnd.nextInt(8)) }
            }
            PracticeScreen(
                title = "Quick Practice", questions = round, onExit = { nav.pop() },
                onFinished = { firstTry, total -> nav.replace(Route.Result(route.key, starsFor(firstTry, total), firstTry, total)) },
            )
        }
        Route.Stars -> StarsScreen()
        Route.Settings -> SettingsScreen()
        Route.Subjects -> SubjectsScreen()
        is Route.SeedClass -> SeedClassScreen(route.subject)
        is Route.SeedChapters -> SeedChaptersScreen(route.subject, route.cls)
        is Route.SeedQuiz -> SeedQuizRoute(route)
        is Route.Result -> ResultScreen(route)
    }
}
