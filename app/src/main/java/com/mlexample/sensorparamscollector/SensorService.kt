package com.mlexample.sensorparamscollector

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


class SensorService : Service(), SensorEventListener {

    val outputPath = "Sensor_Collector"
    val outputRoot = File(Environment.getExternalStorageDirectory(), outputPath)
    var fileFormat = SimpleDateFormat("'sensor_'yyyy-MM-dd-HH-mm-ss'.txt'", Locale.US)
    var outputFile = ""

    val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    var running = false

    override fun onCreate() {
        super.onCreate()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
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

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val sensor = sensorEvent?.sensor
        if(sensor?.type == Sensor.TYPE_ACCELEROMETER){
            Log.e("han.hanh", "onSensor changed")
            saveData(sensorEvent)
        }
    }

    private fun saveData(sensorEvent: SensorEvent?){
        if(!outputRoot.exists() && !outputRoot.mkdirs()){
            return
        }

        val outputName = fileFormat.format(Date())
        outputFile = File(outputRoot, outputName).absolutePath

        val x = sensorEvent!!.values[0]
        val y = sensorEvent!!.values[1]
        val z = sensorEvent!!.values[2]
        val currentTime = System.currentTimeMillis()
        val data = currentTime.toString() + "," + x + "," + y + "," + z

        try {
            val outputStreamWriter = OutputStreamWriter(this.openFileOutput(outputFile, Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }

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
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
    }

    override fun onUnbind(intent: Intent?): Boolean {
        running = false
        return super.onUnbind(intent)
    }
}