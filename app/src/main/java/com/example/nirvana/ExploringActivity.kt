package com.example.nirvana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nirvana.databinding.ActivityExploringBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ExploringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExploringBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val category = intent.getStringExtra("category")
        updateRecyclerView(category)

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
                    //Already in explore, do nothing
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> false
            }
        }
    }
    private fun updateRecyclerView(category: String?) {
        database = FirebaseDatabase.getInstance().getReference("Activities")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val activities = it.children.mapNotNull { snap ->
                    snap.getValue(Activities::class.java)
                }.filter { activity ->
                    category == "ALL" || activity.category == category
                }
                val sortedActivities = activities.sortedBy { it.distance }

                val adapter = ActivitiesAdapter(sortedActivities, this)
                binding.recyclerList.adapter = adapter
                binding.recyclerList.layoutManager = LinearLayoutManager(this)
            } else {
                Toast.makeText(this, "No activities found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
        }
    }

}