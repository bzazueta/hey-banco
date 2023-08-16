package com.sycnos.heyvisitas

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sycnos.heyvisitas.databinding.ActivityHomeLobbyBinding
import com.sycnos.heyvisitas.util.VariablesGlobales
import org.json.JSONObject

class HomeLobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            val i = Intent(this@HomeLobbyActivity, RegisterActivity::class.java)
            startActivity(i)
        }
        binding.tvScannerQR.setOnClickListener {
            val i = Intent(this@HomeLobbyActivity, ScannerQRActivity::class.java)
            startActivity(i)
        }
    }

    override fun onBackPressed() {
        mensajeCerrarSesion("Mensaje","¿Desea Cerrar Sesión?",this@HomeLobbyActivity)

    }

    override fun onResume() {
        super.onResume()
        val sharedPrefRoles: SharedPreferences =
            this@HomeLobbyActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@HomeLobbyActivity.getSharedPreferences(
                "temp", MODE_PRIVATE
            )
        var stringJsonTemp = sharedPrefTemp.getString("temp", "")
        val stringJsonUsuario = sharedPrefRoles.getString("usuario", "")
        if(!stringJsonUsuario.equals(""))
        {
            val jsonUsuario = JSONObject(stringJsonUsuario)
            /** seteamos las variables que vamos a ocupar en todas las pantallas*****///
            VariablesGlobales.setIdUser(jsonUsuario.getJSONObject("user").getString("id"))
            VariablesGlobales.setUser(jsonUsuario.getJSONObject("user").getString("email"))
            VariablesGlobales.setPasw(stringJsonTemp)
            /****fin variables*****/
        }

    }
    fun mensajeCerrarSesion(titulo: String ,mensaje : String,activity : Activity) {
        val builder = AlertDialog.Builder(activity)
        with(builder)
        {
            setTitle(titulo)
            val message = setMessage(mensaje)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener {
                        dialog, id ->
                    val sharedPref: SharedPreferences = this@HomeLobbyActivity.getSharedPreferences(
                        "usuario", MODE_PRIVATE
                    )
                    val sharedPrefTemp: SharedPreferences = this@HomeLobbyActivity.getSharedPreferences(
                        "usuario", MODE_PRIVATE
                    )
                    val editor = sharedPref.edit()
                    val editor2 = sharedPrefTemp.edit()
                    editor.remove("usuario")
                    editor2.remove("temp")
                    editor.commit()
                    val stringJson = sharedPref.getString("usuario", "")
                    stringJson.toString()
                    finish()
                })
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener {
                        dialog, id ->

                })
            show()
        }
    }

}