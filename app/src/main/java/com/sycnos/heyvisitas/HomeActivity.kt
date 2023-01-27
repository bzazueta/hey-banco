package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sycnos.heyvisitas.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvVisit.setOnClickListener{
            val i = Intent(this@HomeActivity, VisitsActivity::class.java)
            startActivity(i)
        }
        binding.tvPendingVisit.setOnClickListener{
            val i = Intent(this@HomeActivity, PendingVisitsActivity::class.java)
            startActivity(i)
        }

        binding.tvProviders.setOnClickListener {
            val i = Intent(this@HomeActivity, ProvidersActivity::class.java)
            startActivity(i)
        }
        binding.tvMessage.setOnClickListener {
            val i = Intent(this@HomeActivity, NoticesmessagesActvity::class.java)
            startActivity(i)
        }
    }


}