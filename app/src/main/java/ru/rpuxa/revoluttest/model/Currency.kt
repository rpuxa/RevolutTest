package ru.rpuxa.revoluttest.model

typealias Rates = Map<Currency, Double>

enum class Currency {
    AUD, BGN, BRL, CAD, CHF, CNY, CZK, DKK, EUR, GBP, HKD, HRK, HUF, IDR, ILS, INR, ISK,
    JPY, KRW, MXN, MYR, NOK, NZD, PHP, PLN, RON, RUB, SEK, SGD, THB, TRY, USD, ZAR;

    companion object {
        val BASE = EUR
    }
}