package com.gumthala.learningapp.tv

import com.gumthala.learningapp.tv.core.Seeds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The English / Hindi / Marathi / Maths question banks bundled in tv/src/main/assets/seed. Regression guard
 * for the bug where every class showed the very same chapters.
 */
class SeedTest {
    private fun seedDir(): File {
        val candidates = listOfNotNull(System.getProperty("tv.assets"), "src/main/assets", "tv/src/main/assets").map { File(it, "seed") }
        return candidates.firstOrNull { it.isDirectory } ?: throw AssertionError("seed assets not found; tried $candidates")
    }

    private fun load(code: String) = Seeds.parse(File(seedDir(), "$code.json").readText(), code)

    @Test
    fun every_subject_has_seven_classes_of_distinct_chapters() {
        for (code in listOf("english", "hindi", "marathi", "maths")) {
            val subject = load(code)
            assertEquals("$code: classes", (1..7).toList(), subject.classes.keys.toList())
            val titles = HashMap<String, Int>()
            for ((level, chapters) in subject.classes) {
                assertTrue("$code class $level: only ${chapters.size} chapters", chapters.size >= 5)
                val ids = chapters.map { it.id }
                assertEquals("$code class $level: duplicate chapter ids", ids.size, ids.toSet().size)
                for (ch in chapters) {
                    val en = ch.title["en"].orEmpty()
                    assertTrue("$code class $level: blank chapter title", en.isNotBlank())
                    val seenIn = titles.put(en, level)
                    assertTrue("$code: chapter \"$en\" appears in class $seenIn and class $level", seenIn == null)
                }
            }
        }
    }

    @Test
    fun every_seed_question_is_answerable_in_every_language() {
        for (code in listOf("english", "hindi", "marathi", "maths")) {
            for ((level, chapters) in load(code).classes) for (ch in chapters) {
                assertTrue("$code class $level ${ch.id}: ${ch.questions.size} questions", ch.questions.size >= 8)
                for (q in ch.questions) {
                    val where = "$code class $level ${ch.id} \"${q.prompt["en"]}\""
                    assertTrue("$where: option count ${q.options.size}", q.options.size in 2..4)
                    assertTrue("$where: answer index", q.correct in q.options.indices)
                    for (lang in listOf("en", "mr", "hi")) {
                        assertTrue("$where: blank prompt in $lang", q.prompt[lang].orEmpty().isNotBlank())
                        val texts = q.options.map { it[lang].orEmpty() }
                        assertTrue("$where: blank or duplicate options in $lang $texts", texts.all { it.isNotBlank() } && texts.toSet().size == texts.size)
                    }
                }
            }
        }
    }

    @Test
    fun language_subjects_do_not_repeat_a_question_between_classes() {
        for (code in listOf("english", "hindi", "marathi")) {
            val seen = HashMap<String, Int>()
            for ((level, chapters) in load(code).classes) {
                if (level == 1) continue // Class 1 is the original bank; Classes 2-7 are new material
                for (ch in chapters) for (q in ch.questions) {
                    val key = q.prompt["hi"] + "|" + q.prompt["mr"] + "|" + q.options[q.correct]["en"]
                    val was = seen.put(key, level)
                    assertTrue("$code: \"${q.prompt["en"]}\" is asked in class $was and class $level", was == null || was == level)
                }
            }
        }
    }
}
