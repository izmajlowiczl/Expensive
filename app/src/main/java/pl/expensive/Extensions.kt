package pl.expensive

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

public fun ViewGroup.inflateLayout(@LayoutRes layout: Int): View =
        LayoutInflater.from(context).inflate(layout, this, false)

