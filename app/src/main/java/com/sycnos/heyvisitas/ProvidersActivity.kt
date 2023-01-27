package com.sycnos.heyvisitas

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.sycnos.heyvisitas.databinding.ActivityProvidersBinding

class ProvidersActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityProvidersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProvidersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener { finish() }

    }

    fun basicAlert(view: View) {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Atención")
            val message = setMessage("Mensaje personalizado se puede personalizar desde la plataforma")
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                })
            show()
        }
    }
}