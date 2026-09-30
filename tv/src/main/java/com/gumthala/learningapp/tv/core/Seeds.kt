package com.gumthala.learningapp.tv.core

import com.gumthala.learningapp.tv.content.Choice
import com.gumthala.learningapp.tv.content.Question
import org.json.JSONObject
import kotlin.random.Random

/*
 * The phone app ships English / Marathi / Hindi / Maths question banks as JSON (assets/seed/). The TV
 * build bundles the very same files and reads them here, so "More subjects" stays identical to the
 * phone app without a second copy to keep in sync. Questions are plain multiple choice.
 */

class SeedQ(val prompt: Map<String, String>, val hint: Map<String, String>, val options: List<Map<String, String>>, val correct: Int)
class SeedChapter(val id: String, val title: Map<String, String>, val questions: List<SeedQ>)
class SeedSubject(val code: String, val name: Map<String, String>, val classes: Map<Int, List<SeedChapter>>)

fun Map<String, String>.localized(lang: String): String =
    this[lang]?.takeIf { it.isNotBlank() } ?: this["en"].orEmpty()

object Seeds {
    private val cache = HashMap<String, SeedSubject>()

    @Synchronized
    fun load(assets: AssetReader, code: String): SeedSubject? {
        cache[code]?.let { return it }
        return try {
            parse(assets.read("seed/$code.json"), code).also { cache[code] = it }
        } catch (e: Exception) {
            null
        }
    }

    private fun texts(o: JSONObject?): Map<String, String> =
        if (o == null) emptyMap() else mapOf("en" to o.optString("en"), "mr" to o.optString("mr"), "hi" to o.optString("hi"))

    internal fun parse(json: String, code: String): SeedSubject {
        val root = JSONObject(json)
        val classes = LinkedHashMap<Int, List<SeedChapter>>()
        val cs = root.getJSONArray("classes")
        for (i in 0 until cs.length()) {
            val c = cs.getJSONObject(i)
            val chapters = ArrayList<SeedChapter>()
            val chs = c.getJSONArray("chapters")
            for (k in 0 until chs.length()) {
                val ch = chs.getJSONObject(k)
                val qs = ArrayList<SeedQ>()
                val qa = ch.getJSONArray("questions")
                for (m in 0 until qa.length()) {
                    val q = qa.getJSONObject(m)
                    val opts = q.getJSONArray("options")
                    val texts = ArrayList<Map<String, String>>()
                    var correct = -1
                    for (o in 0 until opts.length()) {
                        val op = opts.getJSONObject(o)
                        texts.add(texts(op.optJSONObject("text")))
                        if (op.optBoolean("correct")) correct = o
                    }
                    if (correct >= 0 && texts.size >= 2) qs.add(SeedQ(texts(q.optJSONObject("prompt")), texts(q.optJSONObject("hint")), texts, correct))
                }
                chapters.add(SeedChapter(ch.getString("id"), texts(ch.optJSONObject("title")), qs))
            }
            classes[c.getInt("classLevel")] = chapters
        }
        return SeedSubject(code, texts(root.optJSONObject("subject")?.optJSONObject("name")), classes)
    }
}

/** Ten questions from a chapter (or all, if it has fewer), shuffled, in the chosen language. */
fun SeedChapter.toQuestions(lang: String, rnd: Random = Random.Default, max: Int = 10): List<Question> =
    questions.shuffled(rnd).take(max).map { q ->
        val order = q.options.indices.shuffled(rnd)
        val choices = order.map { Choice(q.options[it].localized(lang).ifBlank { "?" }) }
        Question(
            prompt = q.prompt.localized(lang),
            visual = null,
            choices = choices,
            answer = order.indexOf(q.correct),
            hint = q.hint.localized(lang).ifBlank { "Read it again slowly." },
            solution = emptyList(),
        )
    }
