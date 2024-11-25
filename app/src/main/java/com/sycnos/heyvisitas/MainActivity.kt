package com.sycnos.heyvisitas

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.RequestParams
import com.loopj.android.http.TextHttpResponseHandler
import com.sycnos.heyvisitas.databinding.ActivityMainBinding
import com.sycnos.heyvisitas.util.Conexion
import com.sycnos.heyvisitas.util.Mensajes
import com.sycnos.heyvisitas.util.VariablesGlobales
import cz.msebera.android.httpclient.Header
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mensajes: Mensajes? = Mensajes()
    private lateinit var progresoLogin: ProgressDialog
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val TAG: String = MainActivity::class.java.getName()
    var token : String  = ""
    private var conexion: Conexion? = Conexion()
    private val NOTIFICATION_PERMISSION_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1);

            }
            else {

            }
        }


        /***** obtenemos los datos del usuario  */
        val sharedPrefRoles: SharedPreferences =
            this@MainActivity.getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
        val sharedPrefTemp: SharedPreferences =
            this@MainActivity.getSharedPreferences(
                "temp", MODE_PRIVATE
            )
        //obtener json guardado en shared preferences
        var stringJsonTemp = sharedPrefTemp.getString("temp", "")
        val stringJsonUsuario = sharedPrefRoles.getString("usuario", "")
        if(!stringJsonUsuario.equals(""))
        {
            val jsonUsuario = JSONObject(stringJsonUsuario)
            if (jsonUsuario.getString("status") == "true")
            {
                /** seteamos las variables que vamos a ocupar en todas las pantallas*****///
                VariablesGlobales.setIdUser(jsonUsuario.getJSONObject("user").getString("id"))
                VariablesGlobales.setUser(jsonUsuario.getJSONObject("user").getString("email"))
                VariablesGlobales.setPasw(stringJsonTemp)
                /****fin variables*****/

                var idRol = jsonUsuario.getJSONObject("user").getString("rol")
                when(idRol){
                    "2" -> {
                        val i = Intent(this@MainActivity, HomeLobbyActivity::class.java)
                        startActivity(i)}
                    "1" -> {
                        val i = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(i)
                    }else -> {
                    val i = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(i)
                    }
                }
            }
        }
        /***** fin sahredpreferences  */

        //*** Obtain the FirebaseAnalytics instance.***//
        firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ID")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "ONCREATE")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
        bundle.putString("MESSAGE", "oncreate")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        //***Close Obtain the FirebaseAnalytics instance.***//

        //*** Obtain token Firebase.***//

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val x = task.result
            token = task.result

            // Log and toast
            val msg = resources.getString(R.string.msg_token_fmt)
            Log.d(TAG, msg)
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })

       /* quoteViewModel.quoteModel.observe(this, Observer { currentQuote ->
            binding.tvQuote.text = currentQuote.quote
            binding.tvAuthor.text = currentQuote.author
        })*/
        binding.tvForgetPassword.setOnClickListener{
            binding.etUser.setText("")
            binding.etPassword.setText("")
            val i = Intent(this@MainActivity, ForgotPasswordActivity::class.java)
            startActivity(i)
        }

        binding.btnLogin.setOnClickListener{

            try
            {
                var url = getString(R.string.urlDominio)+"/public/api/app/login"
                var validado :Boolean = true
                binding.btnLogin.isEnabled = false
                progresoLogin = ProgressDialog(this@MainActivity)
                progresoLogin.setMessage("Validando información...")
                progresoLogin.setIndeterminate(false)
                progresoLogin.setCancelable(false)
                progresoLogin.show()

                var conectado : Boolean = false
                conectado = conexion!!.isOnline(this)

//                 binding.etUser.setText("guardia001@hey.inc")//calixto.pinon@hey.inc
//                binding.etPassword.setText("123456")//calixto2022

//                binding.etUser.setText("calixto.pinon@hey.inc")//
//                binding.etPassword.setText("calixto2022")//

//                binding.etUser.setText("usuario@hey.inc")//
//                binding.etPassword.setText("123456")//

                if(conectado)
                {
                    if(binding.etUser.text.toString().equals(""))
                    {
                        progresoLogin.dismiss()
                        validado=false
                        binding.btnLogin.isEnabled = true
                        mensajes!!.mensajeAceptar("Mensaje","Favor de ingresar el usuario",this@MainActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()
                    }

                    if(validado) {
                        if (binding.etPassword.text.toString().equals("")) {
                            progresoLogin.dismiss()
                            validado = false
                            binding.btnLogin.isEnabled = true
                            mensajes!!.mensajeAceptar("Mensaje","Favor de ingresar la contraseña",this@MainActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

//                        Toast.makeText(
//                            this@MainActivity,
//                            "Favor de ingresar la contraseña",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        }
                    }

                    if(validado)
                    {
                        try
                        {
                            val params = RequestParams()
                            params.put("email", binding.etUser.text.toString().trim())
                            params.put("password", binding.etPassword.text.toString().trim())
                            login(params)
                        }catch (e: Exception)
                        {
                            binding.btnLogin.isEnabled = true
                            progresoLogin.dismiss()
                            mensajes!!.mensajeAceptar("Mensaje",e.toString(),this@MainActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                        }
                    }
                }
                else{
                    binding.btnLogin.isEnabled = true
                    progresoLogin.dismiss()
                    mensajes!!.mensajeAceptar("Mensaje","Enciende tu conexión a internet",this@MainActivity);                    //Toast.makeText(this@MainActivity,"Favor de ingresar el usuario",Toast.LENGTH_SHORT).show()

                }

            }catch (e: Exception)
            {
                binding.btnLogin.isEnabled = true
                progresoLogin.dismiss()
            }
//             var user = binding.etUser.text.toString()
//              when(user){
//                "lobby" -> {
//                    val i = Intent(this@MainActivity, HomeLobbyActivity::class.java)
//                    startActivity(i)}
//                 "user" -> {
//                     val i = Intent(this@MainActivity, HomeActivity::class.java)
//                     startActivity(i)
//                 }else -> {
//                  val i = Intent(this@MainActivity, HomeActivity::class.java)
//                  startActivity(i)
//                 }
//            }

        }
       // binding.viewContainer.setOnClickListener { quoteViewModel.randomQuote() }

    }

    fun login(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(R.string.urlDominio)+"/public/api/app/login", params, object : TextHttpResponseHandler() {

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String,
                    throwable: Throwable
                ) {
                    throwable.toString()
                    progresoLogin.dismiss()
                    binding.btnLogin.isEnabled = true
                    //var x = responseString

                    mensajes!!.mensajeAceptar(
                        "Mensaje",
                        responseString,
                        this@MainActivity
                    )

                }


                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseString: String
                ) {
                    // progresoCobro.dismiss()
                    var jsonObject: JSONObject? = null
                    try
                    {
                        progresoLogin.dismiss()
                        binding.btnLogin.isEnabled = true
                        jsonObject = JSONObject(responseString)
                        if (jsonObject.getString("status") == "true")
                        {
                            /** seteamos las variables que vamos a ocupar en todas las pantallas*****///
                            VariablesGlobales.setIdUser(jsonObject.getJSONObject("user").getString("id"))
                            VariablesGlobales.setUser(jsonObject.getJSONObject("user").getString("email"))
                            VariablesGlobales.setPasw( binding.etPassword.text.toString().trim())
                            /****fin variables*****///

                            /***** guardar los datos de usuario  */
                            val sharedPrefRoles: SharedPreferences =
                                this@MainActivity.getSharedPreferences(
                                    "usuario", MODE_PRIVATE
                                )
                            val editorRoles = sharedPrefRoles.edit()
                            editorRoles.putString("usuario", jsonObject.toString())
                            editorRoles.commit()

                            val sharedPrefTemp: SharedPreferences =
                                this@MainActivity.getSharedPreferences(
                                    "temp", MODE_PRIVATE
                                )
                            val temp = sharedPrefTemp.edit()
                            temp.putString("temp", binding.etPassword.text.toString().trim())
                            temp.commit()

                            //obtener json guardado en shared preferences
                            val stringJsonUsuario = sharedPrefRoles.getString("usuario", "")
                            val jsonUsuario = JSONObject(stringJsonUsuario)
                            jsonUsuario.length()

                            var stringJsontemp = sharedPrefTemp.getString("temp", "")

                            if (stringJsontemp != null) {
                                stringJsontemp.length
                            }
                            /***** fin sahredpreferences  */

                            /****enviamos el token*****///
                            try
                            {
                                var iduser = jsonObject.getJSONObject("user").getString("id")
                                val params = RequestParams()
                                params.put("id", iduser)
                                params.put("token", token)
                                sendToken(params)
                            }catch (e: Exception)
                            {
                                binding.btnLogin.isEnabled = true
                                progresoLogin.dismiss()
                            }
                            /****fin token*****///
                             binding.etUser.setText("")
                            binding.etPassword.setText("")
                            var idRol = jsonObject.getJSONObject("user").getString("rol")
                            when(idRol){
                                "2" -> {
                                    val i = Intent(this@MainActivity, HomeLobbyActivity::class.java)
                                    startActivity(i)}
                                 "1" -> {
                                     val i = Intent(this@MainActivity, HomeActivity::class.java)
                                     startActivity(i)
                                 }else -> {
                                  val i = Intent(this@MainActivity, HomeActivity::class.java)
                                  startActivity(i)
                                 }
                            }
                        }
                        if (jsonObject.getString("status") == "false") {
                            progresoLogin.dismiss()
                            binding.btnLogin.isEnabled = true
                            mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@MainActivity);
                        }
                    } catch (e: JSONException) {
                        binding.btnLogin.isEnabled = true
                        progresoLogin.dismiss()
                        e.printStackTrace()
                    }
                }
            })
    }

    fun sendToken(params: RequestParams?) {
        val client = AsyncHttpClient()
        //client.addHeader("Cookie", "XSRF-TOKEN=eyJpdiI6IjZuXC90b3BVcU1tbmtDXC9hR2ZzUGJCdz09IiwidmFsdWUiOiJCVGNZYmRoK2hDMVBUUDAzdDM5WDNcL2RNaGtRMUZzS1FibVV4NXpzbkhSNzNES0xXM1RGRUlSOGxkQVwvNm83Z3QiLCJtYWMiOiIyZDgwYjU5ZWJkNDQ5NGMyMzM5ZDg1NzZiYTJjZGI0MGQ5YjllYWJhNTJhMzk2NzhlMzFjMjljZWIxZTBlZDdjIn0%3D; heybanco_session=eyJpdiI6IlU1RDk3SXZ4YVk0cEd2ZkdUTlRvVXc9PSIsInZhbHVlIjoiMkZhZ28wY0JYb1BLalZ6Zk9CZmRqK3F0WTg3cThpZE1OY0dmb2JJSDl6dWRtcjkxMUhQOW0wVFhZM0lzdk5cL1ciLCJtYWMiOiIyZjY5NThhZTdkODllZWVjYmRlNzc4YWE2OGNmOWI1MWU4OTViMzdkODZlZTA4N2I4MWFlODZkOTYxOTExMWE3In0%3D");

        client.post(getString(R.string.urlDominio)+"/public/api/fcm/token", params, object : TextHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers:Array<Header>,
                responseString: String,
                throwable: Throwable
            ) {
                progresoLogin.dismiss()
                binding.btnLogin.isEnabled = true

                mensajes!!.mensajeAceptar(
                    "Mensaje",
                    responseString,
                    this@MainActivity)

            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseString: String
            ) {

                var jsonObject: JSONObject? = null
                try
                {
                    progresoLogin.dismiss()
                    binding.btnLogin.isEnabled = true
                    jsonObject = JSONObject(responseString)
                    if (jsonObject.getString("status") == "true")
                    {
                        //mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@MainActivity);
                    }
                    if (jsonObject.getString("status") == "false") {
                        binding.btnLogin.isEnabled = true
                        mensajes!!.mensajeAceptar("Mensaje",jsonObject.getString("text"),this@MainActivity);
                    }
                } catch (e: JSONException) {
                    binding.btnLogin.isEnabled = true
                    progresoLogin.dismiss()
                    e.printStackTrace()
                }
            }
        })
    }


}

