package com.sycnos.heyvisitas.util

import android.app.Activity
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class SharedPref {

    public fun getUsuario (activity: Activity) : String {

        val sharedPref: SharedPreferences =
            activity.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE
            )
        //****obtener json guardado en shared preferences*****///
        var user =""
        val stringJson = sharedPref.getString("user", "")
        if(!stringJson.toString().equals(""))
        {
            val userJson = JSONObject(stringJson)
            var user =  userJson.getJSONObject("user").getString("email")
        }

        return user

    }

    public fun getPass (activity: Activity) : String {

        val sharedPref: SharedPreferences = activity.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var pasw = sharedPref.getString("password", "")
        return pasw.toString()

    }
}