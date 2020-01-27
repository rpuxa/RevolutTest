package ru.rpuxa.revoluttest.dagger

import dagger.Component
import ru.rpuxa.revoluttest.dagger.providers.ServerProvider
import ru.rpuxa.revoluttest.dagger.providers.ViewModelsProvider
import ru.rpuxa.revoluttest.viewmodel.ViewModelFactory
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ServerProvider::class,
        ViewModelsProvider::class
    ]
)
interface Component {
    fun inject(factory: ViewModelFactory)
}