package com.sycnos.heyvisitas

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sycnos.heyvisitas.databinding.ActivityMainBinding
import com.sycnos.heyvisitas.viewmodel.ViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /* quoteViewModel.quoteModel.observe(this, Observer { currentQuote ->
            binding.tvQuote.text = currentQuote.quote
            binding.tvAuthor.text = currentQuote.author
        })*/
        binding.tvForgetPassword.setOnClickListener{
            val i = Intent(this@MainActivity, ForgotPasswordActivity::class.java)
            startActivity(i)
        }
        binding.btnLogin.setOnClickListener{
             var user = binding.etUser.text.toString()
              when(user){
                "lobby" -> {
                    val i = Intent(this@MainActivity, HomeLobbyActivity::class.java)
                    startActivity(i)}
                 "user" -> {
                     val i = Intent(this@MainActivity, HomeActivity::class.java)
                     startActivity(i)
                 }else -> {
                  val i = Intent(this@MainActivity, HomeActivity::class.java)
                  startActivity(i)
                 }
            }

        }
       // binding.viewContainer.setOnClickListener { quoteViewModel.randomQuote() }

    }
}