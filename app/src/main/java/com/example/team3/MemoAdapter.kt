package com.example.team3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class MemoAdapter (val memoList: ArrayList<MemoData>) : RecyclerView.Adapter<MemoAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View, data: MemoData, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val memoView: TextView = itemView.findViewById(R.id.firstSentence)
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.show_all_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.memoView.text = memoList[position].memo
        holder.date.text = memoList[position].date
    }

    override fun getItemCount(): Int {
        return memoList.size
    }
}