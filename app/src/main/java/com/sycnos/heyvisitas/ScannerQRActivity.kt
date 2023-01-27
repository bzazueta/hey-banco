package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.sycnos.heyvisitas.databinding.ActivityScannerQractivityBinding


class ScannerQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScannerQractivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScannerQractivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnAdd.setOnClickListener {
            /*
            val i = Intent(this@ScannerQRActivity, ScannerProviderActivity::class.java)
            startActivity(i)*/
            initScanner()
        }

    }
    private fun initScanner(){
        IntentIntegrator(this).initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show()
            } else {
                when(result.contents){
                    "provedor" -> {
                        val i = Intent(this@ScannerQRActivity, ScannerProviderActivity::class.java)
                        startActivity(i)}
                    "visita" -> {
                        val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
                        startActivity(i)
                    }else -> {
                    val i = Intent(this@ScannerQRActivity, ScannerVisitActivity::class.java)
                    startActivity(i)
                }
                }
                Toast.makeText(this, "El valor escaneado es: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}