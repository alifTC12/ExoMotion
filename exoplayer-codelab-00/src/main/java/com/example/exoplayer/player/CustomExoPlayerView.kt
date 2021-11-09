package com.example.exoplayer.player

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

internal class CustomExoPlayerView(
    context: Context, attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.actionMasked == MotionEvent.ACTION_MOVE) {
            return true
        }

        return super.onInterceptTouchEvent(ev)
    }
}