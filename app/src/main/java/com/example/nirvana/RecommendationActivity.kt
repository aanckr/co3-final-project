package com.example.nirvana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nirvana.databinding.ActivityExploringBinding
import com.example.nirvana.databinding.ActivityRecommendationBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RecommendationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendationBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val activityName = intent.getStringExtra("activity")

        database = FirebaseDatabase.getInstance().getReference("Activities")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val activities = it.children.mapNotNull { snap ->
                    snap.getValue(Activities::class.java)
                }.filter { activity ->
                    activityName == activity.name
                }
                binding.detailedDescription.text = activities[0].name
                binding.location.text = activities[0].distance.toString() + " km"
                binding.ratingBar.rating = activities[0].rank.toString().toFloat()
                binding.descriptionText.text = activities[0].tags.toString()
                when (activities[0].category){
                    "SIGHTS" -> binding.activityImg.setImageResource(R.drawable.sight)
                    "RESTAURANT" -> binding.activityImg.setImageResource(R.drawable.restaurant)
                    "SHOPPING" -> binding.activityImg.setImageResource(R.drawable.shopping)
                    else -> binding.activityImg.setImageResource(R.drawable.mountains)
                }

            }
        }
    }
}