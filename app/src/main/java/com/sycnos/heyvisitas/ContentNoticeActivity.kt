package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sycnos.heyvisitas.databinding.ActivityContentNoticeBinding

class ContentNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContentNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnFile.setOnClickListener {
            val i = Intent(this@ContentNoticeActivity, SeeFileActivity::class.java)
            startActivity(i)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }

    }


}