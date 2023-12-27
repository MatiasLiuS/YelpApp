package com.example.project_1

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView


class NewsSourceAdapter(var newsSources: List<NewsCategoryBusiness>, private val searchInput: String): RecyclerView.Adapter<NewsSourceAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout){
        val sourceNameText: TextView = rootLayout.findViewById(R.id.source_name)
        val descriptionText: TextView = rootLayout.findViewById(R.id.source)
        val categoryText: TextView = rootLayout.findViewById(R.id.category_text)
        val id: TextView = rootLayout.findViewById(R.id.id)
        val term: TextView = rootLayout.findViewById(R.id.term)
        val cardView: ConstraintLayout = rootLayout.findViewById(R.id.card_view_layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.cardviewlayout, parent, false)
        return ViewHolder(rootLayout)
    }

    override fun getItemCount(): Int {
        return newsSources.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNewsSource = newsSources[position]
        holder.sourceNameText.text = currentNewsSource.name
        holder.descriptionText.text = truncateDescription(currentNewsSource.description)
        holder.categoryText.text =currentNewsSource.category
        holder.id.text =currentNewsSource.id
        val ResultNewsContext=holder.cardView.context

        holder.cardView.setOnClickListener {

            Log.d("NewsSourceAdapter", "Item clicked ${currentNewsSource.id}")
            Log.d("NewsSourceAdapter", "Search Input: $searchInput")

            val intent = Intent(holder.cardView.context, ResultsScreen::class.java)

            intent.putExtra("SEARCHINPUT_EXTRA", searchInput)

            intent.putExtra("id", currentNewsSource.id)
            intent.putExtra("name", currentNewsSource.name)

            holder.cardView.context.startActivity(intent)

        }
    }

     fun updateData(newData: List<NewsCategoryBusiness>) {
         Log.d("NewsSourceAdapter", "Updating data with ${newData.size} items")
         newsSources = newData
        notifyDataSetChanged()
         Log.d("NewsSourceAdapter", "NewsSourceAdapter Updated data. Item count: ${newsSources.size}")

     }


    private fun truncateDescription(description: String): String {
        val maxDescriptionLength = 120
        return if (description.length > maxDescriptionLength) {
            description.take(maxDescriptionLength) + "..."
        } else {
            description
        }
    }
}