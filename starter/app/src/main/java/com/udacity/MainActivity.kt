package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

data class Repo(
    val url: String,
    val name: String
)

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private var selectedRepo: Repo? = null

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(toolbar)

        binding.contentMain.apply {
            customButton.setOnClickListener {
                Log.i("MainActivity", "Custom button pressed")
                if (radioGroup.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        applicationContext, "Please select the file to download",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    notificationManager = ContextCompat.getSystemService(
                        applicationContext,
                        NotificationManager::class.java
                    ) as NotificationManager
                    notificationManager.cancelNotifications()
                    download()
                }
            }

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.glide_download_radiobutton -> selectedRepo = Repo(
                        "https://github.com/bumptech/glide",
                        R.string.glide_download_string.toString()
                    )
                    R.id.loadApp_download_radiobutton -> selectedRepo = Repo(
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter",
                        R.string.load_app_download_string.toString()
                    )
                    R.id.retrofit_download_radiobutton -> selectedRepo = Repo(
                        "https://github.com/square/retrofit",
                        R.string.retrofit_download_string.toString()
                    )
                    else -> selectedRepo = null
                }
            }
        }
        binding.contentMain.customButton.setState(ButtonState.Completed)


        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            notificationManager.sendNotification(
                application.getString(R.string.notification_description),
                applicationContext
            )
            Log.i("MainActivity", "OnReceive called with Download id $notificationManager")
            binding.contentMain.customButton.setState(ButtonState.Completed)

        }
    }

    private fun download() {
        binding.contentMain.customButton.setState(ButtonState.Loading)
        selectedRepo?.let {
            val request =
                DownloadManager.Request(Uri.parse(selectedRepo!!.url))
                    .setTitle(selectedRepo!!.name)
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = R.string.channel_id.toString()
    }
}