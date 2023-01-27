package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sycnos.heyvisitas.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSee.setOnClickListener{
            val i = Intent(this@MessageActivity, MessageHistoryActivity::class.java)
            startActivity(i)
        }
        binding.tvMessageSolved.setOnClickListener {
            val i = Intent(this@MessageActivity, MessageSolvedActivity::class.java)
            startActivity(i)
        }
        binding.btnBack.setOnClickListener { finish() }
    }


}