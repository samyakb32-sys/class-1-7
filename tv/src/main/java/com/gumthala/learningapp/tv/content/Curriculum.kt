package com.gumthala.learningapp.tv.content

/** The full Maths pathway, in teaching order: from "what is counting?" up to Class 7. */
object Curriculum {
    val worlds: List<World> = listOf(
        world1,
        world2,
        world3,
        world4,
        world5,
        world6,
    )

    val lessons: List<Lesson> = worlds.flatMap { it.lessons }

    fun lesson(id: String): Lesson? = lessons.firstOrNull { it.id == id }
    fun world(id: String): World? = worlds.firstOrNull { it.id == id }
    fun worldOf(lessonId: String): World? = worlds.firstOrNull { w -> w.lessons.any { it.id == lessonId } }

    /** The lesson a learner should do next: the first one not yet completed, else the last one. */
    fun next(starsOf: (String) -> Int): Lesson = lessons.firstOrNull { starsOf(it.id) == 0 } ?: lessons.last()
}
