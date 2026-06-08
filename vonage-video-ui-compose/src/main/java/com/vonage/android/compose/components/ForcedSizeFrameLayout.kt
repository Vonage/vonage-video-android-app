package com.vonage.android.compose.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Custom FrameLayout that forces its child (the OpenTok video view) to always
 * fill the container, regardless of any cached dimensions the child might have.
 * 
 * This solves the issue where OpenTok's native video view retains dimensions
 * from a previous container when moved between layouts (e.g., Grid → Active Speaker).
 */
internal class ForcedSizeFrameLayout(context: Context) : FrameLayout(context) {
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        
        val containerWidth = measuredWidth
        val containerHeight = measuredHeight
        
        // Force all children to be measured with our exact dimensions
        // This overrides any cached dimensions the child might have
        val exactWidthSpec = MeasureSpec.makeMeasureSpec(containerWidth, MeasureSpec.EXACTLY)
        val exactHeightSpec = MeasureSpec.makeMeasureSpec(containerHeight, MeasureSpec.EXACTLY)
        
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                // Force the child to measure with our exact dimensions
                child.measure(exactWidthSpec, exactHeightSpec)
                
                // Additionally, clear any minimum dimensions that might prevent proper sizing
                child.minimumWidth = 0
                child.minimumHeight = 0
            }
        }
    }
    
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val containerWidth = measuredWidth
        val containerHeight = measuredHeight
        
        // Layout all children to fill the entire container
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                child.layout(0, 0, containerWidth, containerHeight)
            }
        }
    }
    
    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        
        // Force immediate measure/layout cycle
        child?.let {
            it.forceLayout()
            requestLayout()
        }
    }
}

