package com.example.a13musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast

class MusicPlayerService: Service() {

    var mMediaPlayer: MediaPlayer? = null

    // 바인더 생성
    var mBinder: MusicPlayerBinder = MusicPlayerBinder()

    inner class MusicPlayerBinder: Binder() {
        fun getService(): MusicPlayerService {
            return this@MusicPlayerService
        }
    }

    // 서비스가 생성되면 호출하는 함수
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    // 서비스가 바인드 되었을 때 호출하는 함수
    // 바인더를 반환한다.
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //stopForeground(true)
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    fun startForegroundService() {
        // 안드로이드 오레오(8.0) 이상이면 알림 채널을 만들어야한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val mChannel = NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(mChannel)
        }

        // 알림 생성
        val notification: Notification = Notification.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_play)
            .setContentTitle("뮤직 플레이어 앱")
            .setContentText("앱이 실행중 입니다.")
            .build()

        // 알림 발생
        startForeground(1, notification)
    }

    fun isPlaying(): Boolean {
        return (mMediaPlayer != null && mMediaPlayer?.isPlaying ?: false)
    }

    fun play() {
        // 미디어 플레이어가 없으면 생성
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.puzzle)

            mMediaPlayer?.setVolume(1.0f, 1.0f)
            mMediaPlayer?.isLooping = true
            mMediaPlayer?.start()
        } else {
            if (mMediaPlayer!!.isPlaying) {
                Toast.makeText(this, "이미 음악이 실행 중 입니다.", Toast.LENGTH_SHORT).show()
            } else {
                mMediaPlayer?.start()
            }
        }
    }

    fun pause() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun stop() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mMediaPlayer = null
            }
        }
    }
}