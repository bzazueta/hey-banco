package com.sycnos.heyvisitas.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FormatoFechas {

    private var fecha_ = ""

    public fun formatoFechatoyyyymmdd(fecha: String?): String? {
        try {
            val dateFormat_fecha_pago = SimpleDateFormat("yyyy-M-dd")
            var date_fecha_pago: Date? = null
            date_fecha_pago = dateFormat_fecha_pago.parse(fecha.toString().replace(" ","").trim())
            val dateFormat_d_fecha_pago = SimpleDateFormat("yyyy/MM/dd")
            fecha_ = dateFormat_d_fecha_pago.format(date_fecha_pago)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return fecha_
    }
}