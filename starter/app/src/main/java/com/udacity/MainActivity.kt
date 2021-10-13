package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
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
                    customButton.setState(ButtonState.Clicked)
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
                        application.getString(R.string.glide_download_string)
                    )
                    R.id.loadApp_download_radiobutton -> selectedRepo = Repo(
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter",
                        application.getString(R.string.load_app_download_string)
                    )
                    R.id.retrofit_download_radiobutton -> selectedRepo = Repo(
                        "https://github.com/square/retrofit",
                        application.getString(R.string.retrofit_download_string)
                    )
                    else -> selectedRepo = null
                }
                Log.i("setOnCheckedChangeListener", "$selectedRepo")
            }
        }
        binding.contentMain.customButton.setState(ButtonState.Completed)


        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))


    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            id?.let {
                if (id == downloadID) {
                    // Download matches the expected ID
                    Log.i("onReceive", "Download manager received expected ID $downloadID")
                    val downloadQuery = DownloadManager.Query().setFilterById(id)
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadCursor: Cursor = downloadManager.query(downloadQuery)

                    if (downloadCursor.moveToFirst()) {
                        // Cursor is not empty
                        val status =
                            downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {

                            val url = downloadCursor.getString(
                                downloadCursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                            )
                            Log.i("onReceiver", "Download status succeed: $url")
                            notificationManager.sendNotification(
                                application.getString(R.string.notification_description),
                                applicationContext,
                                url,
                                "Succeed"
                            )
                            binding.contentMain.customButton.setState(ButtonState.Completed)
                        } else {
                            Log.i(
                                "onReceiver",
                                "Download status is not succeed, Column status code: $status"
                            )
                            notificationManager.sendNotification(
                                application.getString(R.string.notification_description),
                                applicationContext,
                                downloadCursor.getString(
                                    downloadCursor.getColumnIndex(
                                        DownloadManager.COLUMN_TITLE
                                    )
                                ),
                                "Fail"
                            )
                            binding.contentMain.customButton.setState(ButtonState.Completed)
                        }
                    } else {
                        Log.i("onReceiver", "Download Cursor is empty for querying id $downloadID")
                        binding.contentMain.customButton.setState(ButtonState.Completed)
                    }

                } else {
                    Log.i(
                        "onReceive",
                        "Download manager received other ID: $id, expected $downloadID"
                    )
                }
            }
        }
    }

    private fun download() {
        binding.contentMain.customButton.setState(ButtonState.Loading)
        Log.i("download", "Selected repo: $selectedRepo")
        selectedRepo?.let {
            val request =
                DownloadManager.Request(Uri.parse(it.url + "/archive/master.zip"))
                    .setTitle(it.name)
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }
    }
}