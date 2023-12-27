package com.example.project_1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class NewsCategoryAdapter(
    context: Context,
    resource: Int,
    private val newsCategories: List<NewsCategoryBusiness>,
    private val onCategorySelected: (NewsCategoryBusiness) -> Unit
) : ArrayAdapter<NewsCategoryBusiness>(context, resource, newsCategories) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private val textViewResourceId: Int = android.R.id.text1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(android.R.layout.simple_spinner_item, parent, false)

        val category = newsCategories[position]
        val textView = view.findViewById<TextView>(textViewResourceId)

        textView.text = category.category

        view.setOnClickListener {
            val selectedCategory = newsCategories[position]
            onCategorySelected(selectedCategory)
        }

        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)

        val category = newsCategories[position]
        val textView = view.findViewById<TextView>(textViewResourceId)

        textView.text = category.category

        return view
    }
}
