package com.example.nirvana

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nirvana.databinding.ActivitySettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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
}