package com.example.nirvana

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ActivitiesAdapter(private val activitiesList: List<Activities>) : RecyclerView.Adapter<ActivitiesAdapter.ActivitiesViewHolder>() {

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
        holder.textViewName.text = currentItem.name
        holder.textViewLocation.text = currentItem.distance.toString() + " km"
        holder.textViewRating.rating = currentItem.rank.toString().toFloat()
        val category = currentItem.category
        if (category == "SIGHTS"){
            holder.imageViewImage.setImageResource(R.drawable.sight)
        } else if (category == "RESTAURANTS"){
            holder.imageViewImage.setImageResource(R.drawable.restaurant)
        } else if (category == "SHOPPING"){
            holder.imageViewImage.setImageResource(R.drawable.shopping)
        } else {
            holder.imageViewImage.setImageResource(R.drawable.mountains)
        }
    }

    override fun getItemCount() = activitiesList.size
}
