package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        Log.i("DetailActivity", "extras: ${intent.extras}")

        val status: String? = intent.getStringExtra("STATUS")
        val url: String? = intent.getStringExtra("URL")


        binding.contentDetail.apply {
            okButton.setOnClickListener {
                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            }

            binding.contentDetail.fileName.text = url
            binding.contentDetail.statusText.text = status
            if(status == "Succeed"){
                binding.contentDetail.statusText.setTextColor(Color.GREEN)
            }
            else
            {
                binding.contentDetail.statusText.setTextColor(Color.RED)
            }
        }

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        Log.i("DetailActivity", "extras: ${intent.extras}")
        Log.i("DetailActivity", "URL: $url")
        Log.i("DetailActivity", "status: $status")
        notificationManager.cancelAll()
    }

}
