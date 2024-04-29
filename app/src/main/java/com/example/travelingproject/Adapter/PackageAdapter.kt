package com.example.travelingproject.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelingproject.Activity.PackageActivity
import com.example.travelingproject.ModelClass.ModelClass
import com.example.travelingproject.ModelClass.Packages
import com.example.travelingproject.R
import com.example.travelingproject.databinding.HomeItemFileBinding

class PackageAdapter(
    var context: Context,
    var packagesList: ArrayList<Packages>,
    var profilesList: ArrayList<ModelClass>
) : RecyclerView.Adapter<PackageAdapter.MyviewHolder>() {
    class MyviewHolder(binding: HomeItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        var lnrdetails = binding.lnrdetails
        var imgfirstimage = binding.imgfirstimage
        var txtName = binding.txtName
        var txtPrice = binding.txtPrice
        var txtDays = binding.txtDays
        var txtNotes = binding.txtNotes
        var imgPackageInfo = binding.imgPackageInfo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.home_item_file, parent, false)
        var holder = MyviewHolder(HomeItemFileBinding.bind(v))
        return holder
    }

    override fun getItemCount(): Int {
        return packagesList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        holder.txtName.text = packagesList[position].name
        if (packagesList[position].imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(packagesList[position].imageUrl[0])
                .into(holder.imgfirstimage)
        } else {
            // Handle the case where imageUrl list is empty
            // For example, you can load a placeholder image
            // Glide.with(holder.itemView.context).load(R.drawable.placeholder_image).into(holder.imgfirstimage)
        }
        holder.txtPrice.text = "₹ " + packagesList[position].price.toString() + "/-"
        holder.txtDays.text = packagesList[position].days.toString()
        holder.txtNotes.text = packagesList[position].notes

        holder.imgPackageInfo.setOnClickListener {
            val i = Intent(holder.itemView.context, PackageActivity::class.java)
            i.putExtra("name", packagesList[position].name)
            i.putExtra("mobile", packagesList[position].mobile)
            i.putExtra("allImageList", packagesList[position].imageUrl)
            i.putExtra("Price", "₹ " + packagesList[position].price + "/-")
            i.putExtra("day", packagesList[position].days)
            i.putExtra("Notes", packagesList[position].notes)
            holder.itemView.context.startActivity(i)
        }

        holder.lnrdetails.setOnClickListener {
            val i = Intent(holder.itemView.context, PackageActivity::class.java)
            i.putExtra("name", packagesList[position].name)
            i.putExtra("mobile", packagesList[position].mobile)
            i.putExtra("allImageList", packagesList[position].imageUrl)
            i.putExtra("Price", "₹ " + packagesList[position].price + "/-")
            i.putExtra("day", packagesList[position].days)
            i.putExtra("Notes", packagesList[position].notes)
            holder.itemView.context.startActivity(i)
        }
    }
}