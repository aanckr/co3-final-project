package com.example.nirvana

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nirvana.databinding.ActivitySettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Bottom navigation bar
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.explore -> {
                    startActivity(Intent(this, ExploringActivity::class.java).apply {
                        putExtra("category", "ALL")
                    })
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.settings -> {
                    //Already in settings, do nothing
                    return@setOnNavigationItemSelectedListener true
                }

                else -> false
            }
        }

        binding.locationSwitch.isChecked = isLocationPermissionGranted()

        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!isLocationPermissionGranted()) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                }
            } else{
                Toast.makeText(this, "Go to settings, to deny access.", Toast.LENGTH_SHORT).show()
            }
            binding.locationSwitch.isChecked = isLocationPermissionGranted()
        }

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            scheduleFridayNotification(isChecked)
        }


    }
    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            binding.locationSwitch.isChecked = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun scheduleFridayNotification(enable: Boolean) {
        if (enable && !isExactAlarmPermissionGranted()) {
            requestExactAlarmPermission()
            return
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)

        val pendingIntentFlag =
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, pendingIntentFlag)

        if (enable) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) { calendar.add(Calendar.DAY_OF_YEAR, 7)
            }
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }
    private fun isExactAlarmPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
        }
        return true
    }

    private fun requestExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intent)
    }

}