package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        //get file name and status
        val fileName = intent?.extras?.getString("File Name")
        val status = intent?.extras?.getString("Status")
        binding.contentDetail.fileStatus = status
        binding.contentDetail.fileNameValue.text = fileName
        binding.contentDetail.okButton.setOnClickListener{
            finish()
        }
    }
}
