package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sycnos.heyvisitas.databinding.ActivityHomeLobbyBinding

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


}