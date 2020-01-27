package ru.rpuxa.revoluttest.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.rpuxa.revoluttest.model.Rates
import ru.rpuxa.revoluttest.model.Server
import java.io.IOException
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val server: Server
) : ViewModel(), LifecycleObserver {

    val ratesMap: LiveData<Rates> get() = _ratesMap
    val state: LiveData<State> get() = _state


    private val _state = MutableLiveData(State.LOADING)
    private val _ratesMap = MutableLiveData<Rates>()
    private var requestJob: Job? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun startToObserveRates() {
        if (requestJob?.isActive == true) return

        requestJob = viewModelScope.launch {
            while (true) {
                 try {
                     _ratesMap.value = server.getRates().rates
                     _state.value = State.LOADED
                } catch (e: IOException) {
                     _state.value = State.NO_CONNECTION
                 }
                delay(REPEAT_INTERVAL_MILLIS)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun stopToObserveRates() {
        requestJob?.cancel()
    }

    enum class State {
        LOADING,
        NO_CONNECTION,
        LOADED
    }

    companion object {
        private const val REPEAT_INTERVAL_MILLIS = 1000L
    }
}