package com.sycnos.heyvisitas

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sycnos.heyvisitas.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
          finish()
        }

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