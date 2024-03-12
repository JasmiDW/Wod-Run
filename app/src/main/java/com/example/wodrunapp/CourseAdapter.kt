package com.example.wodrunapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class CourseAdapter(private var courses: MutableList<CourseEntity>,
                    private val db: AppDatabaseRun,
                    private val mapView: MapView,
                    private val markers: MutableMap<CourseEntity, Marker>) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>()
{


    inner class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.course)
        val dateTextView: TextView = view.findViewById(R.id.date)
        val distanceTextView: TextView = view.findViewById(R.id.distance)
        val timeTextView: TextView = view.findViewById(R.id.time)


        init {
            val deleteButton = itemView.findViewById<TextView>(R.id.button_delete)
            deleteButton?.setOnClickListener {
                val course = courses[adapterPosition]
                Toast.makeText(view.context, "Course ${course.name} supprimée", Toast.LENGTH_SHORT).show()
                GlobalScope.launch (Dispatchers.IO)   {
                    db.courseDao().delete(course.id)

                }

            }
        }
    }

    fun removeItem(position: Int) {
        courses.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        holder.nameTextView.text = course.name
        holder.dateTextView.text = course.date
        holder.distanceTextView.text = String.format("%s (km)", course.distance.toString())
        holder.timeTextView.text = String.format("%s (minutes)", course.time.toString())
    }

    override fun getItemCount() = courses.size
}