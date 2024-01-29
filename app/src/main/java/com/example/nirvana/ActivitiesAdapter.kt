package com.example.nirvana

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ActivitiesAdapter(private val activitiesList: List<Activities>, private val context: Context) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivitiesViewHolder>() {

    class ActivitiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.exploring_name)
        val textViewLocation: TextView = view.findViewById(R.id.location)
        val textViewRating: RatingBar = view.findViewById(R.id.ratingBar)
        val imageViewImage: ShapeableImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ActivitiesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val currentItem = activitiesList[position]
        val name = currentItem.name?.let {
            if (it.length > 23) it.substring(0, 23) + "..." else it
        }
        holder.textViewName.text = name
        holder.textViewLocation.text = currentItem.distance.toString() + " km"
        holder.textViewRating.rating = currentItem.rank.toString().toFloat()

        when (currentItem.category) {
            "SIGHTS" -> holder.imageViewImage.setImageResource(R.drawable.sight)
            "RESTAURANT" -> holder.imageViewImage.setImageResource(R.drawable.restaurant)
            "SHOPPING" -> holder.imageViewImage.setImageResource(R.drawable.shopping)
            else -> holder.imageViewImage.setImageResource(R.drawable.mountains)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, RecommendationActivity::class.java).apply {
                putExtra("activity", currentItem.name)
            }
            context.startActivity(intent)
        }
    }
    override fun getItemCount() = activitiesList.size
}
