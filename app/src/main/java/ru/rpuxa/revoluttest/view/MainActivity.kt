package ru.rpuxa.revoluttest.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.rpuxa.revoluttest.R
import ru.rpuxa.revoluttest.model.Currency
import ru.rpuxa.revoluttest.viewModel
import ru.rpuxa.revoluttest.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycle.addObserver(mainViewModel)

        val adapter = RatesAdapter(this)

        mainViewModel.ratesMap.observe(this) { rates ->
            adapter.updateRates(rates + BASE_RATE)
        }

        mainViewModel.state.observe(this) { state: MainViewModel.State ->
            when (state) {
                MainViewModel.State.LOADING -> {
                    loading.isVisible = true
                    no_connection.isVisible = false
                    rates_list.isVisible = false
                }
                MainViewModel.State.NO_CONNECTION -> {
                    loading.isVisible = false
                    no_connection.isVisible = true
                    rates_list.isVisible = false
                }

                MainViewModel.State.LOADED -> {
                    loading.isVisible = false
                    no_connection.isVisible = false
                    rates_list.isVisible = true
                }
            }
        }

        rates_list.layoutManager = LinearLayoutManager(this)
        rates_list.adapter = adapter
    }

    companion object {
        private val BASE_RATE = Currency.BASE to 1.0
    }
}
