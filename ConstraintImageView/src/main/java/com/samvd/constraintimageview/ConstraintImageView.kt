package com.samvd.constraintimageview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.imageview.ShapeableImageView

//
// Created by Dinh Sam Vu on 6/16/22.
//

class ConstraintImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr) {

    companion object {
        const val CONSTRAINT_BY_WIDTH = 0
        const val CONSTRAINT_BY_HEIGHT = 1
        const val CONSTRAINT_BY_SMALLER = 2
        const val CONSTRAINT_BY_LARGER = 3
    }

    private var constraintBy: Int = CONSTRAINT_BY_WIDTH
    private var constraintRatio: Float = 1f

    init {
        val styleable = context.obtainStyledAttributes(attrs, R.styleable.ConstraintImageView)

        /**
         * The constraints in the styleable are represented by integers
         * 0 ~ width, 1 ~  height, 2 ~ smaller, 3 ~ larger
         */
        constraintBy = styleable.getInt(R.styleable.ConstraintImageView_civ_constraint_by, CONSTRAINT_BY_WIDTH)

        /**
         * Constraint ratio is represented by "W:H" (String)
         * If input is invalid, default ratio will be 1:1 (square)
         */
        val rawRatio = styleable.getString(R.styleable.ConstraintImageView_civ_constraint_ratio)
        if (!rawRatio.isNullOrBlank()) {
            val regex = "\\d+:\\d+".toRegex()
            if (regex.matches(rawRatio.toString())) {
                try {
                    constraintRatio =
                        rawRatio.substringBefore(":").toFloat() / rawRatio.substringAfter(":")
                            .toFloat()
                } catch (e: NumberFormatException) {
                    logError(e)
                }
            }
        }
        styleable.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        if(constraintBy == CONSTRAINT_BY_SMALLER){
            constraintBy = if (measuredWidth < measuredHeight) {
                CONSTRAINT_BY_WIDTH
            } else {
                CONSTRAINT_BY_HEIGHT
            }
        }
        if(constraintBy == CONSTRAINT_BY_LARGER){
            constraintBy = if (measuredWidth > measuredHeight) {
                CONSTRAINT_BY_WIDTH
            } else {
                CONSTRAINT_BY_HEIGHT
            }
        }
        if (constraintBy == CONSTRAINT_BY_WIDTH) {
            setMeasuredDimension(measuredWidth, (measuredWidth / constraintRatio).toInt())
        } else {
            setMeasuredDimension((measuredHeight * constraintRatio).toInt(), measuredHeight)
        }
    }

    private fun logError(throwable: Throwable) {
        Log.d("ConstraintImageView", "-------------------------------------------------------------------")
        Log.d("ConstraintImageView", "Error: " + throwable.localizedMessage)
        Log.d("ConstraintImageView", "Error: " + throwable.message)
        throwable.printStackTrace()
        Log.d("ConstraintImageView", "-------------------------------------------------------------------")
    }
}