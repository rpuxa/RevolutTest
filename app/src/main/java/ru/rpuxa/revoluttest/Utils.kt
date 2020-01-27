package ru.rpuxa.revoluttest

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import ru.rpuxa.revoluttest.viewmodel.ViewModelFactory

inline fun <reified VM : ViewModel> ComponentActivity.viewModel() =
    viewModels<VM>(::ViewModelFactory)
