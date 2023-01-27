package com.sycnos.heyvisitas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sycnos.heyvisitas.databinding.ActivityMessageSolvedBinding

class MessageSolvedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageSolvedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageSolvedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
    }
}