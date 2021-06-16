package com.rpv.msm.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerAdapter<VH : RecyclerView.ViewHolder>(
    private var mData: Collection<*>,
    private val layoutId: Int,
    private val viewHolderClass: Class<out RecyclerView.ViewHolder>
) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return viewHolderClass.constructors[0].newInstance(view) as VH
    }

    override fun getItemCount() = mData.size

    fun setData(data: Collection<*>) {
        mData = data
        notifyDataSetChanged()
    }
}
