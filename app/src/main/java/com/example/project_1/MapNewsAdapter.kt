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

class MapNewsAdapter(var mapNews: List<MapNewsBusiness>): RecyclerView.Adapter<MapNewsAdapter.ViewHolder>() {
    class ViewHolder(rootLayout: View): RecyclerView.ViewHolder(rootLayout){
        val thumbnail: ImageView = rootLayout.findViewById(R.id.Thumbnail)
        val newsTitle: TextView = rootLayout.findViewById(R.id.NewsTitle)
        val source: TextView = rootLayout.findViewById(R.id.source)
        val description: TextView = rootLayout.findViewById(R.id.description)
        val cardView: CardView = rootLayout.findViewById(R.id.cardView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val rootLayout: View = layoutInflater.inflate(R.layout.mapcardviewlayout, parent, false)
        val viewHolder = ViewHolder(rootLayout)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mapNews.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNewsSource = mapNews[position]
        val source = currentNewsSource.source
        val url = currentNewsSource.url
        val MapNewsContext=holder.cardView.context
        val sourceName = source.name
        holder.newsTitle.text = truncateDescription(currentNewsSource.title, 20)
        holder.source.text = sourceName
        holder.description.text = truncateDescription(currentNewsSource.description, 100)

        // ng Picasso
        Picasso.get()
            .load(currentNewsSource.urlToImage)
            .into(holder.thumbnail)

        holder.cardView.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            MapNewsContext.startActivity(intent)
        }
    }


    fun updateData(newData: List<MapNewsBusiness>) {
        Log.d("MapNewsAdapter", "Updating data with ${newData.size} items")
        mapNews = newData
        notifyDataSetChanged()
        Log.d("MapNewsAdapter", "MapNewsAdapter Updated data. Item count: ${mapNews.size}")

    }




    private fun truncateDescription(description: String, maxLength: Int): String {
        return if (description.length > maxLength) {
            description.take(maxLength) + "..."
        } else {
            description
        }
    }

}