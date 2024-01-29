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
        updateRecyclerView()
    }
    private fun updateRecyclerView(){
        database = FirebaseDatabase.getInstance().getReference("Activities")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val activities = it.children.map { snap ->
                    snap.getValue(Activities::class.java) ?: Activities()
                }
                val sortedActivities = activities.sortedBy { it.distance }
                val adapter = ActivitiesAdapter(sortedActivities)
                binding.recyclerList.adapter = adapter
                binding.recyclerList.layoutManager = LinearLayoutManager(this)

                Toast.makeText(this, "Activity added srollview", Toast.LENGTH_SHORT).show()
            }
        }
    }
}