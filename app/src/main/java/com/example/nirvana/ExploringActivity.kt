package com.example.nirvana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.nirvana.databinding.ActivityExploringBinding
import com.example.nirvana.databinding.ActivityScrollViewBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ExploringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExploringBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exploring)
    }
    private fun updateScrollView(){
        database = FirebaseDatabase.getInstance().getReference("Activities")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val activities = it.children.map { it.getValue(Activities::class.java)!! }
                val sortedActivities = activities.sortedBy { it.distance }
                binding.recyclerViewActivities.removeAllViews()
                for (activity in sortedActivities) {
                    val activityBinding = recylerViewActivities.inflate(layoutInflater)
                    val text = activity.name?.let {
                        if (it.length > 23) it.substring(0, 23) + "..." else it
                    }
                    activityBinding.exploringName.text = text
                    activityBinding.location.text = activity.distance.toString() + " km"
                    activityBinding.imageView.setImageResource(R.drawable.mountains)
                    activityBinding.ratingBar.rating = activity.rating.toFloat()
                    activityBinding.root.setOnClickListener{
                        //navigationToActivity(activity.name)
                    }

                    binding.rexyclerViewActivities.addView(activityBinding.root)
                }
                Toast.makeText(this, "Activity added srollview", Toast.LENGTH_SHORT).show()
            }
        }
    }
}