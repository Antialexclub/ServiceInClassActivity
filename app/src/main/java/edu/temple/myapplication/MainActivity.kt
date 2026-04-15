package edu.temple.myapplication

import android.R.attr.defaultValue
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timerTextView: TextView
    var timerBinder: TimerService.TimerBinder? = null
    var isConnected = false

    private val defaultValue = 20

    private val timeHandler = Handler(Looper.getMainLooper()) {
        msg -> timerTextView.text = msg.what.toString()
        true
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder?.setHandler(timeHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerBinder = null
            isConnected = false

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerDisplay)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            if (isConnected) {
                val savedValue = timerBinder?.getSavedValue() ?: -1
                val startValue = if (savedValue != -1) savedValue else defaultValue
                timerBinder?.start(startValue)
            }
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isConnected){
                timerBinder?.pause()
            }
        }


        fun onDestroy() {
            unbindService(serviceConnection)
            super.onDestroy()
        }





    }
    override fun onStart(){
        super.onStart()
        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop(){
        super.onStop()
        if (isConnected){
            unbindService(serviceConnection)
        }
        isConnected = false
    }



}


