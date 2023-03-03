package com.sycnos.heyvisitas.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog

class Mensajes {

    fun mensajeAceptar(titulo: String ,mensaje : String,activity : Activity) {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            setTitle(titulo)
            val message = setMessage(mensaje)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                })
            show()
        }
    }

    fun mensajeAceptarExpandableList(titulo: String ,mensaje : String,activity : Context) {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            setTitle(titulo)
            val message = setMessage(mensaje)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                })
            show()
        }
    }
}