package com.gumthala.learningapp.tv

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gumthala.learningapp.tv.core.Platform
import com.gumthala.learningapp.tv.platform.AndroidAssets
import com.gumthala.learningapp.tv.platform.AndroidNarrator
import com.gumthala.learningapp.tv.platform.AndroidSfx
import com.gumthala.learningapp.tv.platform.AndroidStore
import com.gumthala.learningapp.tv.ui.TvApp

/** The only screen-level Android class. No login: it opens straight onto the home screen. */
class TvActivity : ComponentActivity() {
    private var narrator: AndroidNarrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // A lesson can sit on one screen for minutes: don't let the TV's screensaver start mid-lesson.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val voice = AndroidNarrator(this)
        narrator = voice
        val platform = Platform(voice, AndroidSfx(), AndroidStore(this), AndroidAssets(this))
        setContent { TvApp(platform) }
    }

    override fun onStop() {
        narrator?.stop()
        super.onStop()
    }

    override fun onDestroy() {
        narrator?.shutdown()
        super.onDestroy()
    }
}
