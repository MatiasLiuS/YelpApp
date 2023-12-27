package com.example.project_1

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
//STOP UHH
class THAdapter(var thNews: List<THBusiness>,): RecyclerView.Adapter<THAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout){
        val thumbnail: ImageView = rootLayout.findViewById(R.id.Thumbnail)
        val newsTitle: TextView = rootLayout.findViewById(R.id.NewsTitle)
        val source: TextView = rootLayout.findViewById(R.id.source)
        val description: TextView = rootLayout.findViewById(R.id.description)
        val category: TextView = rootLayout.findViewById(R.id.category)
        val cardView: CardView = rootLayout.findViewById(R.id.cardView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.thcardlayout, parent, false)
        val viewHolder = ViewHolder(rootLayout)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return thNews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNewsSource = thNews[position]
        val source = currentNewsSource.source
        val url = currentNewsSource.url
        val MapNewsContext=holder.cardView.context
        val sourceName = source.name
        holder.newsTitle.text = truncateDescription(currentNewsSource.title, 20)
        holder.source.text = sourceName
        holder.description.text = truncateDescription(currentNewsSource.description, 100)
        Log.d("THAdapter", "Category Value: ${currentNewsSource.category}")
        Log.d("THAdapter", "Category TextView Value (Before): ${holder.category.text}")
        Picasso.get()
            .load(currentNewsSource.urlToImage)
            .into(holder.thumbnail)

        holder.cardView.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            MapNewsContext.startActivity(intent)
        }
    }


    fun updateTHData(newData: List<THBusiness> ,) {
        Log.d("MapNewsAdapter", "Updating data with ${newData.size} items")
        thNews = newData
        notifyDataSetChanged()
        Log.d("MapNewsAdapter", "MapNewsAdapter Updated data. Item count: ${thNews.size}")

    }




    private fun truncateDescription(description: String?, maxLength: Int): String {
        return if (description != null && description.length > maxLength) {
            description.take(maxLength) + "..."
        } else if (description != null) {
            description
        } else {
            ""
        }
    }
}