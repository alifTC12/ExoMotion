package com.example.exoplayer.home

import androidx.recyclerview.widget.RecyclerView

// Used to help restore the state of RV inside of RV
interface NestedRecyclerViewViewHolder {
    fun getId(): String
    fun getLayoutManager(): RecyclerView.LayoutManager?
}