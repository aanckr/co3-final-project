package com.example.nirvana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
                    startActivity(Intent(this, ExploringActivity::class.java))
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
    }
}