package ru.rpuxa.revoluttest.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rate.view.*
import ru.rpuxa.revoluttest.R
import ru.rpuxa.revoluttest.model.Currency
import ru.rpuxa.revoluttest.model.Rates
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs


class RatesAdapter(
    private val activity: Activity
) : RecyclerView.Adapter<RatesAdapter.RatesViewHolder>() {


    private var base = Currency.BASE
    private var baseValue = 100.0
    private var rates: Rates? = null
    private var values: Rates? = null
    private val editTexts = LinkedList<WeakEditText>()

    fun updateRates(rates: Rates) {
        synchronized(this) {
            this.rates = rates
            updateValues()
        }
    }

    private fun updateBaseValue(value: Double) {
        synchronized(this) {
            if (baseValue == value) return
            baseValue = value
            updateValues()
        }
    }

    private fun updateBase(base: Currency) {
        synchronized(this) {
            if (this.base == base) return
            val newBasePos = getCurrencyPosition(base)
            this.base = base
            notifyItemMoved(newBasePos, 0)
            notifyItemRangeChanged(0, SIZE)
        }
    }

    private fun updateEditText(editText: EditText, currency: Currency) {
        synchronized(this) {
            editTexts.removeAll { it.needToDelete(editText, currency) }
            editTexts.add(WeakEditText(editText, currency))
            updateEditTextValue(currency)
        }
    }


    private fun updateValues() {
        rates?.let { rates ->
            val values = HashMap<Currency, Double>()
            val baseRate = rates[base] ?: 1.0
            rates.forEach { (currency, value) ->
                values[currency] = value * baseValue / baseRate
            }

            val toUpdate = HashSet<Currency>()

            if (this.values == null) {
                toUpdate += Currency.values()
            } else {
                values.forEach { (currency, value) ->
                    if (abs(value - this.values!![currency]!!) > 1e-5) {
                        toUpdate += currency
                    }
                }
            }

            this.values = values

            toUpdate.forEach(::updateEditTextValue)
        }
    }

    private fun updateEditTextValue(currency: Currency) {
        if (currency == base) return
        val weak = editTexts.find { it.currency == currency } ?: return
        weak.editText.get()?.setText(decimalFormat.format(values!![currency]), null)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
        return RatesViewHolder(view)
    }

    override fun getItemCount(): Int = SIZE

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RatesViewHolder, position: Int) {
        val currency = getCurrencyByPosition(position)
        holder.name.text = currency.name
        holder.valueEditText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                true
            } else {
                false
            }
        }


        val clickListener = View.OnClickListener {
            baseValue = holder.valueEditText.text.toString().toDouble()
            holder.valueEditText.requestFocusFromTouch()
            openKeyboard()
            updateBase(currency)
        }

        val watcher = BaseTextWatcher(currency)

        holder.valueEditText.removeTextChangedListener(watcher)
        holder.valueEditText.addTextChangedListener(watcher)

        if (currency == base) {
            if (!holder.valueEditText.isFocusableInTouchMode) {
                holder.valueEditText.setText(decimalFormat.format(baseValue), null)
                holder.valueEditText.setSelection(holder.valueEditText.text.length)
                holder.itemView.setOnClickListener(clickListener)
                holder.valueEditText.setOnClickListener(null)
                holder.valueEditText.isFocusableInTouchMode = true
            }
        } else {
            holder.valueEditText.isFocusableInTouchMode = false
            holder.valueEditText.removeTextChangedListener(watcher)
            holder.itemView.setOnClickListener(clickListener)
            holder.valueEditText.setOnClickListener(clickListener)
        }
        updateEditText(holder.valueEditText, currency)
    }

    private fun getCurrencyPosition(currency: Currency): Int {
        return when {
            currency == base -> 0
            currency.ordinal <= base.ordinal -> currency.ordinal + 1
            else -> currency.ordinal
        }
    }

    private fun getCurrencyByPosition(position: Int): Currency {
        val ordinal = when {
            position == 0 -> base.ordinal
            position <= base.ordinal -> position - 1
            else -> position
        }
        return Currency.values()[ordinal]
    }

    private val onScroll = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            hideKeyboard()
        }
    }
    private var recycler: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(onScroll)
        recycler = recyclerView

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(onScroll)
    }

    private fun hideKeyboard() {
        val inputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity.currentFocus ?: return
        inputManager.hideSoftInputFromWindow(
            currentFocus.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun openKeyboard() {
        hideKeyboard()
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    class RatesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.currency_textview
        val valueEditText: EditText = view.value_edittext
    }

    private class WeakEditText(
        editText: EditText,
        val currency: Currency
    ) {
        val editText = WeakReference(editText)

        fun needToDelete(editText: EditText, currency: Currency): Boolean {
            val thisEditText = this.editText.get() ?: return true
            return thisEditText == editText || this.currency == currency
        }
    }

    private inner class BaseTextWatcher(private val currency: Currency) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (currency == base) {
                updateBaseValue(
                    s.toString().toDoubleOrNull() ?: 0.0
                )
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun equals(other: Any?): Boolean =
            other is BaseTextWatcher

        override fun hashCode(): Int {
            return 0
        }
    }

    companion object {
        private val SIZE = Currency.values().size
        private val decimalFormat = DecimalFormat("#.###")
    }
}