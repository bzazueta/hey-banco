package com.sycnos.heyvisitas

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerDialogFragment : DialogFragment() {

    private var listener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return activity?.let { DatePickerDialog(it, listener, year, month, day) }!!
    }

    companion object {
        fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerDialogFragment {
            val fragment = DatePickerDialogFragment()
            fragment.listener = listener
            return fragment
        }
    }

}