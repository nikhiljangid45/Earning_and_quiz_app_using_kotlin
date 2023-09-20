package com.example.earningapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.earningapp.QizeActivity
import com.example.earningapp.databinding.CategoryitemBinding
import com.example.earningapp.model.CategoryModelClass

class CategoryAdapter(var categoryList: ArrayList<CategoryModelClass>, val requireActivity: FragmentActivity) : RecyclerView.Adapter<CategoryAdapter.MyCategoryViewHolder>() {

    class MyCategoryViewHolder(var binding : CategoryitemBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(  parent: ViewGroup,  viewType: Int   ): CategoryAdapter.MyCategoryViewHolder {

        return MyCategoryViewHolder(CategoryitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CategoryAdapter.MyCategoryViewHolder, position: Int) {

        val dataList = categoryList[position]
        holder.binding.categoryImage.setImageResource(dataList.catImage)
        holder.binding.category.text = dataList.catText
        holder.binding.buttonCategory.setOnClickListener{
            var intent = Intent(requireActivity,QizeActivity::class.java)
            intent.putExtra("categoryImage",dataList.catImage)
            intent.putExtra("questionType",dataList.catText)
            requireActivity.startActivity(intent)
        }
    }

    override fun getItemCount() = categoryList.size
}