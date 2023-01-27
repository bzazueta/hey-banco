package com.sycnos.heyvisitas

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sycnos.heyvisitas.databinding.ActivityScannerProviderBinding

class ScannerProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)


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