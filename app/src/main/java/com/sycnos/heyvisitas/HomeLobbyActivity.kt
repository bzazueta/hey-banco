package com.sycnos.heyvisitas

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        binding.btnExit.setOnClickListener {
            //mensajeCerrarSesion("Mensaje","¿Desea Cerrar Sesión?",this@HomeActivity)
            basicAlert("Mensaje","¿Desea Cerrar Sesión?")
        }
    }

    override fun onBackPressed() {
        //mensajeCerrarSesion("Mensaje","¿Desea Cerrar Sesión?",this@HomeLobbyActivity)
        basicAlert("Mensaje","¿Desea Cerrar Sesión?")
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

    fun basicAlert(titulo: String ,mensaje : String) {


        val builder: AlertDialog.Builder = AlertDialog.Builder(this@HomeLobbyActivity)
        val inflater = this@HomeLobbyActivity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.dialog_custom2, null, false)

        builder.setView(customView)
        builder.setCancelable(false)
        val lblReglamento =  customView.findViewById<View>(R.id.lblReglamento) as TextView
        val lblAceptar = customView.findViewById<View>(R.id.lblAceptar) as TextView
        val lblTexto = customView.findViewById<View>(R.id.lblTexto) as TextView
        val lblLink = customView.findViewById<View>(R.id.lblLink) as TextView
        lblLink.text = "CANCELAR"
        lblReglamento.text = titulo
        lblTexto.text= mensaje
        lblTexto.setMovementMethod(ScrollingMovementMethod())
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        lblAceptar.setOnClickListener {

            val sharedPref: SharedPreferences = this@HomeLobbyActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.remove("usuario")
            editor.remove("temp")
            editor.commit()
            val stringJson = sharedPref.getString("usuario", "")
            stringJson.toString()
            alertDialog.dismiss()
            finish()


        }

        lblLink.setOnClickListener {
            alertDialog.dismiss()
        }
    }


}