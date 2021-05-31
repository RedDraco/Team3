package com.example.team3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class IconAdapter(val context: Context, val iconList : ArrayList<IconData>) :
        RecyclerView.Adapter<IconAdapter.ViewHolder>(){

    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view:View, iconData: IconData, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iconImage : ImageView =itemView.findViewById(R.id.iconimageView)

        fun bind(iconData: IconData, context: Context){
            val resourceId = context.resources.getIdentifier(iconData.photo, "drawable", context.packageName)
            iconImage.setImageResource(resourceId)
        }

        init {
            itemView.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, iconList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return iconList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(iconList[position], context)
    }
}