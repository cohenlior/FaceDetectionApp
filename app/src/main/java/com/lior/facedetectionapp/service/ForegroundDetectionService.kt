package com.lior.facedetectionapp.service

import android.app.*
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lior.facedetectionapp.repository.FaceDetectionRepository
import com.lior.facedetectionapp.ui.activity.MainActivity
import android.content.Intent
import com.lior.facedetectionapp.R
import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lior.facedetectionapp.utils.LOCAL_BROADCAST_ACTION

const val CHANNEL_ID = "ForegroundServiceChannel"

const val NOTIFICATION_ID = 555

class ForegroundDetectionService : LifecycleService(), LifecycleObserver {

    private lateinit var repository: FaceDetectionRepository
    private var isAppInBackground = false

    override fun onCreate() {
        super.onCreate()
        Log.d("FaceDetectionService", "Service started")

        get().lifecycle.addObserver(this)

        repository = FaceDetectionRepository.getInstance(applicationContext)

        val maxSize = repository.imageGalleryList.value?.size

        repository.searchFacesProcess()

        createNotificationChannel()

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSound(null)
            .setVibrate(longArrayOf(0L))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentTitle(getString(R.string.detecting_faces))
            .setContentText(getString(R.string.detection_in_progress))
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)

        maxSize?.let {

            startForeground(NOTIFICATION_ID, mBuilder.build())

            this.repository.result.observe(this, Observer {
                mBuilder.setProgress(100, (it.first * 100) / maxSize, false)
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
                if (it.first == maxSize) {
                    this.stopSelf()
                    if (isAppInBackground) {
                        mBuilder.setContentTitle(getString(R.string.detection_complete))
                            .setProgress(0, 0, false)
                            .setContentText(getString(R.string.detect_result_msg, it.second, maxSize))
                        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
                    } else {
                        notificationManager.cancel(NOTIFICATION_ID)
                        sendCompletionBroadcast(maxSize, it.second)
                    }
                }
            })
        }
    }

    private fun sendCompletionBroadcast(totalImages: Int, faces: Int) {
        Intent(LOCAL_BROADCAST_ACTION).also { intent ->
            intent.putExtra(getString(R.string.faces_detected), faces)
            intent.putExtra(getString(R.string.total_images), totalImages)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH

            )
            serviceChannel.enableVibration(false)
            serviceChannel.setSound(null, null)
            serviceChannel.vibrationPattern = longArrayOf(0L)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        isAppInBackground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        isAppInBackground = true
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        this.stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FaceDetectionService", "Service Destroyed")
    }
}