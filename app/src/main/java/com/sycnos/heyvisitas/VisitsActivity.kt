package com.sycnos.heyvisitas

import android.Manifest
import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sycnos.heyvisitas.databinding.ActivityVisitsBinding
import java.util.*


class VisitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVisitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDate.setOnClickListener{
            showDatePickerDialog()
        }

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnSelect.setOnClickListener {
            val i = Intent(this@VisitsActivity, PickIdentification::class.java)
            startActivity(i)
        }

    }
    private fun showDatePickerDialog() {

        val newFragment = DatePickerDialogFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
            // +1 because January is zero
            val selectedDate = day.toString() + " / " + (month + 1) + " / " + year
            binding.tvDate.setText(selectedDate)
        })

        newFragment.show(supportFragmentManager, "datePicker")
    }

}