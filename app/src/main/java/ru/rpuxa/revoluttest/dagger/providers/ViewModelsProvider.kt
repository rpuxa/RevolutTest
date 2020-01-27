package ru.rpuxa.revoluttest.dagger.providers

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.rpuxa.revoluttest.dagger.ViewModelKey
import ru.rpuxa.revoluttest.viewmodel.MainViewModel

@Module
abstract class ViewModelsProvider {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun main(v: MainViewModel): ViewModel
}