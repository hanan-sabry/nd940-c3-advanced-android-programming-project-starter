package com.udacity

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var selectedUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        binding.mainLayout.fileRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedUrl = when (checkedId) {
                R.id.option1 -> URL_1
                R.id.option2 -> URL_2
                R.id.option3 -> URL_3
                else -> null
            }
        }
        binding.mainLayout.customButton.setOnClickListener {
            if (null == selectedUrl) {
                Toast.makeText(this, getString(R.string.no_file_to_download_err_msg), Toast.LENGTH_SHORT).show()
            } else {
                download(selectedUrl!!)
            }
        }

        notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createChannel(
            getString(R.string.channel_id),
            getString(R.string.channel_name),
            getString(R.string.channel_desc),
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                val status = getDownloadFileStatus(id)

                val notificationBundle = Bundle().apply {
                    putString("File Name", selectedUrl)
                    putString("Status", status)
                }

                notificationManager.sendNotification(
                    applicationContext,
                    getString(R.string.notification_title),
                    "$selectedUrl is downloaded",
                    notificationBundle
                )
            }
        }
    }

    private fun download(url: String) {
        try {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)


            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } catch (e: Exception) {
            Toast.makeText(this, "File Download Failed", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    private fun getDownloadFileStatus(id: Long): String?{
        val q = DownloadManager.Query()
        q.setFilterById(id)
        val c: Cursor = downloadManager.query(q)
        if (c.moveToFirst()) {
            val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            return when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.success)
                DownloadManager.STATUS_FAILED -> getString(R.string.failed)
                else -> getString(R.string.failed)
            }
        }
        return getString(R.string.failed)
    }


    companion object {
        private const val URL_1 =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_2 =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_3 =
            "https://github.com/square/retrofit/archive/master.zip"
    }

}
