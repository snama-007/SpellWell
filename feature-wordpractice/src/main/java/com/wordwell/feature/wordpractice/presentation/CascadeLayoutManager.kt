package com.wordwell.feature.wordpractice.presentation

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CascadeLayoutManager(context: Context) : LinearLayoutManager(context) {
    private val cascadeOffset = 32 // Offset in pixels for each item
    private val maxElevation = 8f // Maximum elevation for the first item

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        if (recycler == null) return

        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child != null) {
                // Apply cascade offset
                child.translationX = cascadeOffset * (childCount - 1 - i).toFloat()
                // Apply decreasing elevation
                child.elevation = maxElevation * (1 - (i.toFloat() / childCount))
            }
        }
    }

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
        val width = View.MeasureSpec.getSize(widthSpec)
        val height = View.MeasureSpec.getSize(heightSpec)
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        setMeasuredDimension(width, height)
    }
} 