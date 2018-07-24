package com.mlexample.sensorparamscollector

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Binder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permissions

class MainActivity : AppCompatActivity() {

    lateinit var startBtn : Button
    val PERMISSION_REQUEST_CODE = 1343
    var service: SensorService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn = start_btn
        startBtn.setOnClickListener { startSensorService() }
        if(service != null && service!!.isServiceRunning()){
            startBtn.setText("Stop Collecting")
        }
        requestPermission()
    }

    private fun startSensorService(){
        if(service == null || !service!!.isServiceRunning()) {
            bindService(Intent(this, SensorService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
            startBtn.setText("Stop Collecting")
        } else {
            stopService(Intent(this, SensorService::class.java))
            startBtn.setText("Start Collecting")
            service = null
        }
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
            unbindService(this)
        }

        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            service = (binder as SensorService.LocalBinder).getService()
        }
    }

    private fun requestPermission(){
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permission: Array<String>, grantResult: IntArray) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (i in permission.indices) {
                if (permission[i] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (grantResult[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


}
