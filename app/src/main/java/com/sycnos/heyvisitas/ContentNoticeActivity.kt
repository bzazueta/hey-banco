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
    private var titulo : String = ""
    private var cuerpo : String = ""
    private var id     : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContentNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titulo =if (intent.getStringExtra("titulo") == null) "" else intent.getStringExtra("titulo")!!
        cuerpo =if (intent.getStringExtra("cuerpo") == null) "" else intent.getStringExtra("cuerpo")!!
        id =if (intent.getStringExtra("id") == null) "" else intent.getStringExtra("id")!!

        binding.tvMessage.setText(titulo)
        binding.tvMessage2.setText(cuerpo)

        binding.btnFile.setOnClickListener {
            val i = Intent(this@ContentNoticeActivity, SeeFileActivity::class.java)
            i.putExtra("id",id)
            startActivity(i)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }

    }


}