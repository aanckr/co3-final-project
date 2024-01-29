package com.example.nirvana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nirvana.databinding.ActivityExploringBinding
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

                Toast.makeText(this, "Activities added to RecyclerView", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No activities found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
        }
    }

}