package com.arakadds.arak.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.arakadds.arak.R
import com.arakadds.arak.databinding.ItemUserInterestsBinding
import com.arakadds.arak.model.new_mapping_refactore.store.categories.StoreCategory
import com.arakadds.arak.presentation.activities.authentication.ChooseInterestsActivity
import com.bumptech.glide.Glide


class UserInterestsAdapter(
    private val context: Context,
    categoryList: ArrayList<StoreCategory>,
    chooseInterestsActivity: ChooseInterestsActivity,
) :
    RecyclerView.Adapter<UserInterestsAdapter.CategoryViewHolder>() {
    private val categoryList: List<StoreCategory> = categoryList

    private val arrayCategoryIds= arrayListOf<Int>()

    private val chooseInterestsActivity =chooseInterestsActivity

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Use View Binding to inflate the layout
        val binding: ItemUserInterestsBinding =
            ItemUserInterestsBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(@NonNull holder: CategoryViewHolder, position: Int) {

        var category: StoreCategory = categoryList.get(position)

        holder.binding.categoryName.setText(category.nameEn)
        Glide.with(context).load(category.iconUrl.toString())
            .into(holder.binding.categoryIcon)

        holder.binding.categoryLiner.setOnClickListener {

            holder.binding.categoryLiner.setBackgroundResource(R.drawable.item_background_selected)

            arrayCategoryIds.add(category.id)

        }
        chooseInterestsActivity.binding.btnContinue.setOnClickListener {
            chooseInterestsActivity.userInterests(arrayCategoryIds)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(binding: ItemUserInterestsBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        // Use View Binding
        var binding: ItemUserInterestsBinding = binding
    }
}

