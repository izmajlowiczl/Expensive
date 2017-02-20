package pl.expensive

import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.*

fun View.rotate(from: Float, to: Float): Animation {
    return RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f).apply {
        duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        fillAfter = true
        fillBefore = true
    }
}

fun View.scaleFromMiddle(from: Float, to: Float, interpolator: Interpolator = AccelerateDecelerateInterpolator()): Animation {
    return ScaleAnimation(from, to, from, to, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f).apply {
        setInterpolator(interpolator)
        duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        fillAfter = true
    }
}

fun View.expandDown(interpolator: Interpolator = AccelerateDecelerateInterpolator()): Animation {
    measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val targetHeight = measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    visibility = View.VISIBLE

    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            layoutParams.height = if (interpolatedTime == 1f) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                (targetHeight * interpolator.getInterpolation(interpolatedTime)).toInt()
            }
            requestLayout()
        }

        override fun willChangeBounds(): Boolean = true
    }

    // 1dp/ms
    animation.duration = (targetHeight / context.resources.displayMetrics.density).toInt().toLong()
    return animation
}

fun View.collapseUp(interpolator: Interpolator = LinearInterpolator()): Animation {
    val initialHeight = measuredHeight

    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                visibility = GONE
            } else {
                layoutParams.height = initialHeight - (initialHeight * interpolator.getInterpolation(interpolatedTime)).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean = true

    }

    // 1dp/ms
    animation.duration = (initialHeight / context.resources.displayMetrics.density).toInt().toLong()
    return animation
}

fun Animation.endAction(action: () -> Unit) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationEnd(animation: Animation?) = action()

        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    })
}

