package com.gumthala.learningapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gumthala.learningapp.data.local.dao.AdminDao
import com.gumthala.learningapp.data.local.dao.BadgeDao
import com.gumthala.learningapp.data.local.dao.ChapterDao
import com.gumthala.learningapp.data.local.dao.QuestionDao
import com.gumthala.learningapp.data.local.dao.QuizDao
import com.gumthala.learningapp.data.local.dao.SlideDao
import com.gumthala.learningapp.data.local.dao.StudentDao
import com.gumthala.learningapp.data.local.dao.StudentStatsDao
import com.gumthala.learningapp.data.local.dao.SubjectDao
import com.gumthala.learningapp.data.local.dao.TeacherDao
import com.gumthala.learningapp.data.local.entity.AdminEntity
import com.gumthala.learningapp.data.local.entity.BadgeEntity
import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.local.entity.QuizAnswerEntity
import com.gumthala.learningapp.data.local.entity.QuizAttemptEntity
import com.gumthala.learningapp.data.local.entity.SlideDeckEntity
import com.gumthala.learningapp.data.local.entity.SlideEntity
import com.gumthala.learningapp.data.local.entity.StudentEntity
import com.gumthala.learningapp.data.local.entity.StudentStatsEntity
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.local.entity.TeacherEntity

@Database(
    entities = [
        StudentEntity::class,
        TeacherEntity::class,
        AdminEntity::class,
        SubjectEntity::class,
        ChapterEntity::class,
        QuestionEntity::class,
        QuizAttemptEntity::class,
        QuizAnswerEntity::class,
        StudentStatsEntity::class,
        BadgeEntity::class,
        SlideDeckEntity::class,
        SlideEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LearningDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun adminDao(): AdminDao
    abstract fun subjectDao(): SubjectDao
    abstract fun chapterDao(): ChapterDao
    abstract fun questionDao(): QuestionDao
    abstract fun quizDao(): QuizDao
    abstract fun studentStatsDao(): StudentStatsDao
    abstract fun badgeDao(): BadgeDao
    abstract fun slideDao(): SlideDao

    companion object {
        @Volatile private var instance: LearningDatabase? = null

        fun getInstance(context: Context): LearningDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LearningDatabase::class.java,
                    "learning_app.db"
                ).build().also { instance = it }
            }
    }
}
