package com.example.nirvana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_collections -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Collections", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_contact_us -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Contact Us", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_log_out -> {
                    val intent = Intent(this, StartActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        // Profile
        val profileImg: View = findViewById(R.id.profile_img)
        profileImg.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if (toggle.onOptionsItemSelected(item)) {
           return true
       }
        return super.onOptionsItemSelected(item)
    }
}