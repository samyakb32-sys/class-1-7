package com.gumthala.learningapp

import android.app.Application
import com.gumthala.learningapp.data.seed.ContentSeeder
import com.gumthala.learningapp.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LearningApp : Application() {

    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        appScope.launch {
            ContentSeeder.seedIfNeeded(container.contentRepository, container.slideRepository)
            // Bootstrap credentials for the very first Admin login; change immediately after signing in.
            container.authRepository.ensureDefaultAdminExists(
                defaultEmail = "admin@classapp.local",
                defaultPassword = "Admin@123"
            )
        }
    }
}
