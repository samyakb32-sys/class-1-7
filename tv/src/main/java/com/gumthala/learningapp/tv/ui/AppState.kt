package com.gumthala.learningapp.tv.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.gumthala.learningapp.tv.content.Curriculum
import com.gumthala.learningapp.tv.content.Lesson
import com.gumthala.learningapp.tv.core.KeyValueStore
import com.gumthala.learningapp.tv.core.Platform

/**
 * The one learner this TV belongs to. No accounts: whoever is holding the remote is "the student".
 * Stars are remembered per lesson, best result wins, and can be wiped from Settings.
 */
class Progress(private val store: KeyValueStore) {
    private val stars = mutableStateMapOf<String, Int>()

    var voice by mutableStateOf(store.getBool("voice", true))
        private set
    var effects by mutableStateOf(store.getBool("effects", true))
        private set
    var slowVoice by mutableStateOf(store.getBool("slow", true))
        private set

    /** Language of the question banks under "More subjects": en / mr / hi. */
    var seedLanguage by mutableStateOf(store.getString("seedLang", "en"))
        private set

    init {
        for (l in Curriculum.lessons) {
            val s = store.getInt("stars:${l.id}", 0)
            if (s > 0) stars[l.id] = s
        }
    }

    fun starsOf(lessonId: String): Int = stars[lessonId] ?: 0

    val totalStars: Int get() = stars.values.sum()

    val maxStars: Int get() = Curriculum.lessons.size * 3

    /** Keeps the best result; a worse retry never takes stars away. */
    fun record(lessonId: String, earned: Int) {
        if (earned > starsOf(lessonId)) {
            stars[lessonId] = earned
            store.putInt("stars:$lessonId", earned)
        }
    }

    fun nextLesson(): Lesson = Curriculum.next { starsOf(it) }

    // The "More subjects" question banks use the same store but are read when their screen opens,
    // so they need no observable state.
    fun chapterStars(chapterId: String): Int = store.getInt("chapter:$chapterId", 0)
    fun recordChapter(chapterId: String, earned: Int) {
        if (earned > chapterStars(chapterId)) store.putInt("chapter:$chapterId", earned)
    }

    fun changeVoice(on: Boolean) { voice = on; store.putBool("voice", on) }
    fun changeEffects(on: Boolean) { effects = on; store.putBool("effects", on) }
    fun changeSlowVoice(on: Boolean) { slowVoice = on; store.putBool("slow", on) }
    fun changeSeedLanguage(code: String) { seedLanguage = code; store.putString("seedLang", code) }

    fun reset() {
        stars.clear()
        store.clearAll()
        voice = true; effects = true; slowVoice = true; seedLanguage = "en"
    }
}

/** Every screen. [key] round-trips through saved state so the TV can restore where it was. */
sealed class Route(val key: String) {
    data object Home : Route("home")
    data object Map : Route("map")
    data class World(val id: String) : Route("world:$id")
    data class Hub(val lessonId: String) : Route("hub:$lessonId")
    data class Learn(val lessonId: String) : Route("learn:$lessonId")
    data class Practice(val lessonId: String) : Route("practice:$lessonId")
    data object Mix : Route("mix")
    data object Stars : Route("stars")
    data object Settings : Route("settings")
    data object Subjects : Route("subjects")
    data class SeedClass(val subject: String) : Route("seedclass:$subject")
    data class SeedChapters(val subject: String, val cls: Int) : Route("seedchapters:$subject:$cls")
    data class SeedQuiz(val subject: String, val cls: Int, val chapterId: String) : Route("seedquiz:$subject:$cls:$chapterId")

    /** [retry] is the route key to open again for "Try again". */
    data class Result(val retry: String, val stars: Int, val correct: Int, val total: Int) :
        Route("result|$retry|$stars|$correct|$total")

    companion object {
        fun parse(key: String): Route? = try {
            when {
                key == "home" -> Home
                key == "map" -> Map
                key == "mix" -> Mix
                key == "stars" -> Stars
                key == "settings" -> Settings
                key == "subjects" -> Subjects
                key.startsWith("world:") -> World(key.substringAfter(':'))
                key.startsWith("hub:") -> Hub(key.substringAfter(':'))
                key.startsWith("learn:") -> Learn(key.substringAfter(':'))
                key.startsWith("practice:") -> Practice(key.substringAfter(':'))
                key.startsWith("seedclass:") -> SeedClass(key.substringAfter(':'))
                key.startsWith("seedchapters:") -> key.split(':').let { SeedChapters(it[1], it[2].toInt()) }
                key.startsWith("seedquiz:") -> key.split(':').let { SeedQuiz(it[1], it[2].toInt(), it.drop(3).joinToString(":")) }
                key.startsWith("result|") -> key.split('|').let { Result(it[1], it[2].toInt(), it[3].toInt(), it[4].toInt()) }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}

/** A plain back stack. Back pops; there is no login to get stuck behind. */
class Navigator(initial: List<Route> = listOf(Route.Home)) {
    val stack = mutableStateListOf<Route>().apply { addAll(initial) }
    val current: Route get() = stack.last()

    /** Which item was focused on each screen, so coming back lands where you left. */
    val focusMemory = HashMap<String, Int>()

    fun push(route: Route) { stack.add(route) }
    fun replace(route: Route) { stack[stack.lastIndex] = route }
    fun pop(): Boolean {
        if (stack.size <= 1) return false
        stack.removeAt(stack.lastIndex)
        return true
    }
    fun home() { while (stack.size > 1) stack.removeAt(stack.lastIndex); }

    companion object {
        val Saver = listSaver<Navigator, String>(
            save = { nav -> nav.stack.map { it.key } },
            restore = { keys -> Navigator(keys.mapNotNull { Route.parse(it) }.ifEmpty { listOf(Route.Home) }) },
        )
    }
}

/** Handy bundle so screens don't each take five parameters. */
class Env(val platform: Platform, val progress: Progress, val nav: Navigator) {
    val narrator get() = platform.narrator

    /** Speak for real when the voice is on; otherwise wait about as long as reading would take. */
    suspend fun speak(text: String) {
        if (progress.voice) {
            narrator.slow = progress.slowVoice
            narrator.say(text)
        } else {
            kotlinx.coroutines.delay(com.gumthala.learningapp.tv.core.speakingTimeMs(text))
        }
    }

    fun tick() { if (progress.effects) platform.sfx.tick() }
    fun correct() { if (progress.effects) platform.sfx.correct() }
    fun wrong() { if (progress.effects) platform.sfx.wrong() }
    fun star() { if (progress.effects) platform.sfx.star() }
    fun fanfare() { if (progress.effects) platform.sfx.fanfare() }
}

val LocalEnv = staticCompositionLocalOf<Env> { error("Env not provided") }
