package com.mlexample.sensorparamscollector

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.security.Permissions

class MainActivity : AppCompatActivity() {

    lateinit var startBtn : Button
    lateinit var userId : EditText
    lateinit var activityType : Spinner
    val PERMISSION_REQUEST_CODE = 1343
    companion object {
        var service: SensorService? = null
    }
    lateinit var preferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        startBtn = start_btn
        userId = user_id
        userId.setText(preferences.getString("user_id", ""))
        activityType = activity_type
        activityType.setSelection(getIndex(activityType, preferences.getString("activity_type","")))
        startBtn.setOnClickListener { startSensorService() }
        if(service != null && service!!.isServiceRunning()){
            startBtn.setText("Stop Collecting")
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermission()
    }

    private fun getIndex(spinner: Spinner, value: String): Int{
        for (i in 0..spinner.count) {
            if(spinner.getItemAtPosition(i).toString().equals(value))
                return i
        }
        return 0
    }

    private fun startSensorService(){
        if(service == null || !service!!.isServiceRunning()) {
            if(userId.text.isEmpty()){
                Toast.makeText(this, "Please input user id first", Toast.LENGTH_SHORT).show()
            } else {
                preferences.edit().putString("user_id", userId.text.toString()).apply()
                preferences.edit().putString("activity_type", activityType.selectedItem.toString()).apply()
                startService(Intent(this, SensorService::class.java))
                bindService(Intent(this, SensorService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
                startBtn.setText("Stop Collecting")
            }
        } else {
            service?.stopForeground(true)
            stopService(Intent(this, SensorService::class.java))
            unbindService(serviceConnection)
            startBtn.setText("Start Collecting")
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

    @RequiresApi(Build.VERSION_CODES.M)
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

    override fun onDestroy() {
        if(service != null) {
            try {
                unbindService(serviceConnection)
            } catch (e : IllegalArgumentException){

            }
        }
        super.onDestroy()
    }

}
