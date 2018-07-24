package com.mlexample.sensorparamscollector

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder

class SensorService : Service(), SensorEventListener {

    val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    var running = false

    override fun onCreate() {
        super.onCreate()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
        running = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(p0: SensorEvent?) {

    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder(){
        fun getService() : SensorService{
            return this@SensorService
        }
    }

    fun isServiceRunning() : Boolean{
        return running
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        running = false
    }
}