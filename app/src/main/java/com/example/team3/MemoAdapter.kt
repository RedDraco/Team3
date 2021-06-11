package com.example.team3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemoAdapter (val memoList: ArrayList<memoData>) : RecyclerView.Adapter<MemoAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val memoView: TextView = itemView.findViewById(R.id.memo_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.show_all_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.memoView.text = memoList[position].memo
    }

    override fun getItemCount(): Int {
       return memoList.size
    }
}