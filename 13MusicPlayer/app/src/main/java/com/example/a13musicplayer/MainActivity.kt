package com.example.a13musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btn_play: Button
    lateinit var btn_pause: Button
    lateinit var btn_stop: Button

    var mService: MusicPlayerService? = null

    // 익명으로 서비스 연결 객체 생성
    val mServiceConnection = object : ServiceConnection {
        // 서비스 바인드가 완료되면 호출
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mService = (binder as MusicPlayerService.MusicPlayerBinder).getService()
        }

        // 서비스가 예기치 못하게 종료될 경우 호출
        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        btn_play = findViewById(R.id.btn_play)
        btn_pause = findViewById(R.id.btn_pause)
        btn_stop = findViewById(R.id.btn_stop)

        btn_play.setOnClickListener(this)
        btn_pause.setOnClickListener(this)
        btn_stop.setOnClickListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_play -> {
                play()
            }

            R.id.btn_pause -> {
                pause()
            }

            R.id.btn_stop -> {
                stop()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 앱이 처음으로 실행된다면
        if (mService == null) {
            // 안드로이드 오레오(8.0) 이상이면 startForegroundService()로 서비스 실행
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForegroundService(Intent(this, MusicPlayerService::class.java))
            }
            // 아니라면 startService()로 서비스 실행
            else {
                startService(Intent(applicationContext, MusicPlayerService::class.java))
            }

            // 액티비티를 서비스와 바인드 한다.
            val intent = Intent(this, MusicPlayerService::class.java)
            // mServiceConnection를 같이 전달하여 서비스 바인드가 완료된 이후 onServiceConnected를 호출해준다.
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun play() {
        mService?.play()
    }

    fun pause() {
        mService?.pause()
    }

    fun stop() {
        mService?.stop()
    }
}