package ru.rpuxa.revoluttest.view

import android.app.Application
import ru.rpuxa.revoluttest.dagger.Component
import ru.rpuxa.revoluttest.dagger.DaggerComponent

class App : Application() {

    companion object {
        val component: Component = DaggerComponent.builder().build()
    }
}