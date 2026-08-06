package com.gumthala.learningapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        SubjectEntity::class,
        ChapterEntity::class,
        QuestionEntity::class,
        OptionEntity::class,
        QuizAttemptEntity::class,
        AttemptAnswerEntity::class,
        ProgressEntity::class,
        BadgeEntity::class,
        SlideDeckEntity::class,
        SlideEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contentDao(): ContentDao
    abstract fun attemptDao(): AttemptDao
    abstract fun progressDao(): ProgressDao
    abstract fun badgeDao(): BadgeDao
    abstract fun slideDao(): SlideDao

    companion object {
        const val NAME = "learning_course.db"
    }
}
