package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sycnos.heyvisitas.databinding.ActivityNoticesmessagesActvityBinding

class NoticesmessagesActvity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticesmessagesActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoticesmessagesActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.tvNotice.setOnClickListener{
            val i = Intent(this@NoticesmessagesActvity, NoticesActivity::class.java)
            startActivity(i)
        }
        binding.tvMessages.setOnClickListener{
            val i = Intent(this@NoticesmessagesActvity, MessageActivity::class.java)
            startActivity(i)
        }
    }


}