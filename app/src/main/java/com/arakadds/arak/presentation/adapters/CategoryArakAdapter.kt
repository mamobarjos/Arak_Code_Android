package com.arakadds.arak.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.databinding.ItemCategoryBinding
import com.arakadds.arak.model.arakStoreModel.StoreCategoryData
import com.arakadds.arak.presentation.activities.home.fragments.ArakStoreFragment
import com.bumptech.glide.Glide


class CategoryArakAdapter(
    private val context: Context,
    categoryList: List<StoreCategoryData>,
    arakStoreFragment: ArakStoreFragment,
    language: String,
    arakStoreCallBacks: ArakStoreCallBacks
) :
    RecyclerView.Adapter<CategoryArakAdapter.CategoryViewHolder>() {
    private val categoryList: List<StoreCategoryData> = categoryList
    private val arakStoreFragment: ArakStoreFragment = arakStoreFragment
    private val language: String = language
    private val arakStoreCallBacks: ArakStoreCallBacks = arakStoreCallBacks

    interface ArakStoreCallBacks {
        fun onAllCategoriesSelected()
        fun onCategorySelected(categoryId: Int)
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Use View Binding to inflate the layout
        val binding: ItemCategoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: CategoryViewHolder, position: Int) {
        val category: StoreCategoryData = categoryList[position]
        if (category.id == -1) {
            holder.binding.categoryIcon.setImageResource(R.drawable.all_category_icon)
            if (language == "en") {
                holder.binding.categoryName.text = category.name.toString()
            } else {
                holder.binding.categoryName.text = category.nameAr.toString()
            }
            holder.binding.categoryLiner.setOnClickListener {
                arakStoreCallBacks.onAllCategoriesSelected()
            }
        } else {
            try {
                // Bind data to the views
                Glide.with(context).load(category.image!!.src.toString())
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.binding.categoryIcon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.binding.categoryName.text = category.name.toString()
            holder.binding.categoryLiner.setOnClickListener {
                category.id?.let { arakStoreCallBacks.onCategorySelected(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        // Use View Binding
        var binding: ItemCategoryBinding = binding
    }
}

