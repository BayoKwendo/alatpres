package com.alat.model

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class TextViewDatePicker @JvmOverloads
    constructor(context: Context,
                private val mView: TextView, minDate: Long = 0, maxDate: Long = 0) : View.OnClickListener, OnDateSetListener {
     var datePickerDialog: DatePickerDialog? = null
        private set
    private val mContext: Context
    private var mMinDate: Long
    private var mMaxDate: Long
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = monthOfYear
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val date = calendar.time
        @SuppressLint("SimpleDateFormat") val formatter = SimpleDateFormat(DATE_SERVER_PATTERN)
        mView.text = formatter.format(date)
    }

    override fun onClick(v: View) {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        datePickerDialog = DatePickerDialog(mContext,
                AlertDialog.THEME_HOLO_LIGHT, this, calendar[Calendar.YEAR],
                calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
        datePickerDialog!!.datePicker.maxDate = System.currentTimeMillis() - 568025136000L
        datePickerDialog!!.show()
    }

    fun setMinDate(minDate: Long) {
        mMinDate = minDate
    }

    fun setMaxDate(maxDate: Long) {
        mMaxDate = maxDate
    }

    companion object {
        const val DATE_SERVER_PATTERN = "dd-MM-yyyy"
    }

    init {
        mView.setOnClickListener(this)
        mView.isFocusable = false
        mContext = context
        mMinDate = minDate
        mMaxDate = maxDate
    }
}