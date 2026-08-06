package com.gumthala.learningapp.di

import android.content.Context
import com.gumthala.learningapp.data.local.LearningDatabase
import com.gumthala.learningapp.data.remote.firebase.FirestoreSyncManager
import com.gumthala.learningapp.data.remote.tts.QuestionAudioPlayer
import com.gumthala.learningapp.data.repository.AuthRepository
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.data.repository.ProgressRepository
import com.gumthala.learningapp.data.repository.QuizRepository
import com.gumthala.learningapp.data.repository.RosterRepository
import com.gumthala.learningapp.data.repository.SlideRepository
import com.gumthala.learningapp.data.session.SessionManager

/**
 * Simple hand-rolled DI container (no Hilt/Dagger) — the object graph here is small and
 * entirely first-party, so a service locator keeps things easy to read without extra
 * annotation-processing machinery.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: LearningDatabase by lazy { LearningDatabase.getInstance(appContext) }
    val sessionManager: SessionManager by lazy { SessionManager(appContext) }
    val syncManager: FirestoreSyncManager by lazy { runCatching { FirestoreSyncManager() }.getOrElse { FirestoreSyncManager() } }

    val authRepository: AuthRepository by lazy {
        AuthRepository(database.studentDao(), database.teacherDao(), database.adminDao())
    }

    val contentRepository: ContentRepository by lazy {
        ContentRepository(appContext, database.subjectDao(), database.chapterDao(), database.questionDao(), syncManager)
    }

    val progressRepository: ProgressRepository by lazy {
        ProgressRepository(database.studentStatsDao(), database.badgeDao(), database.quizDao())
    }

    val quizRepository: QuizRepository by lazy {
        QuizRepository(appContext, database.quizDao(), progressRepository, syncManager)
    }

    val slideRepository: SlideRepository by lazy { SlideRepository(database.slideDao()) }

    val rosterRepository: RosterRepository by lazy {
        RosterRepository(database.studentDao(), database.teacherDao())
    }

    fun newQuestionAudioPlayer(): QuestionAudioPlayer = QuestionAudioPlayer(appContext)
}
