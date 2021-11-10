package com.example.exoplayer.player

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

internal class HeaderMoviesBehaviour : CoordinatorLayout.Behavior<AppBarLayout>() {

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: AppBarLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        val headerHeight = child.layoutParams.height
        println("onNestedScroll headerHeight: " + headerHeight)
        println("onNestedScroll dyConsumed: " + dyConsumed)
        println("onNestedScroll dyUnconsumed: " + dyUnconsumed)
    }

}