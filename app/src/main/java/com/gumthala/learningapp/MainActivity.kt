package com.gumthala.learningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.gumthala.learningapp.di.LocalAppContainer
import com.gumthala.learningapp.ui.AppRoot
import com.gumthala.learningapp.ui.theme.LearningAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as LearningApp).container
        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                LearningAppTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppRoot()
                    }
                }
            }
        }
    }
}
