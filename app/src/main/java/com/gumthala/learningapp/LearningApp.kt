package com.gumthala.learningapp

import android.app.Application
import com.gumthala.learningapp.data.seed.SeedLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LearningApp : Application() {

    @Inject lateinit var seedLoader: SeedLoader

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { seedLoader.seedIfNeeded() }
    }
}
