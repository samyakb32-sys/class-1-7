package com.gumthala.learningapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

/** Tiny generic factory so screen ViewModels can take plain constructor args without Hilt. */
@Composable
inline fun <reified VM : ViewModel> rememberViewModel(crossinline builder: () -> VM): VM =
    viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = builder() as T
    })
