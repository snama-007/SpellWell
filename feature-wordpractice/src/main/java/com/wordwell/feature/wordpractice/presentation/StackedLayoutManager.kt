package com.wordwell.feature.wordpractice.presentation

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

class StackedLayoutManager : LinearLayoutManager {
    private val maxRotation = 15f
    private val maxTranslation = 120f
    private val scaleFactor = 0.15f
    private val stackHeight = 900f // Increased height for larger cards

    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        updateItemDecorations()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            updateItemDecorations()
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scroll = super.scrollVerticallyBy(dy, recycler, state)
        updateItemDecorations()
        return scroll
    }

    private fun updateItemDecorations() {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child?.let {
                val position = getPosition(it)
                val childTop = getDecoratedTop(it)
                
                // Only apply stack effect to items in the top area
                if (childTop < stackHeight) {
                    val progress = min(1f, childTop / stackHeight)
                    
                    // Calculate rotation
                    val rotation = maxRotation * (1 - progress)
                    it.rotation = rotation
                    
                    // Calculate translation
                    val translation = maxTranslation * (1 - progress)
                    it.translationY = translation
                    
                    // Calculate scale
                    val scale = 1f - (scaleFactor * progress)
                    it.scaleX = scale
                    it.scaleY = scale
                    
                    // Calculate alpha
                    val alpha = 1f - (0.3f * progress)
                    it.alpha = alpha
                } else {
                    // Reset transformations for items below stack area
                    it.rotation = 0f
                    it.translationY = 0f
                    it.scaleX = 1f
                    it.scaleY = 1f
                    it.alpha = 1f
                }
            }
        }
    }
} 