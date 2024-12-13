package com.example.driverattentiveness.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.driverattentiveness.R

class PasswordEditText : AppCompatEditText {

    private lateinit var clearButtonImage: Drawable
    private var isPasswordVisible = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        maxLines = 1
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (s.length < 8) {
                    error = context.getString(R.string.need_more_char_8)
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8) {
                    error = context.getString(R.string.need_more_char_8)
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.length < 8) {
                    error = context.getString(R.string.need_more_char_8)
                }
            }
        })

        // Set the initial password toggle icon
        setDrawableEndIcon()

        // Set up the click listener for the password visibility toggle
        setOnTouchListener { _, event ->
            if (compoundDrawables[2] != null && event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = compoundDrawables[2]
                val width = width
                val touchWidth = (width - paddingRight - drawableEnd.bounds.width())

                if (event.x > touchWidth) {
                    togglePasswordVisibility()
                    performClick()
                }
            }
            false
        }
    }

    private fun setDrawableEndIcon() {
        // Set initial password visibility icon (invisible)
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.password_icon_invisible) ?: return
        setCompoundDrawablesWithIntrinsicBounds(null, null, clearButtonImage, null)
    }

    private fun togglePasswordVisibility() {
        // Toggle the password visibility state
        isPasswordVisible = !isPasswordVisible

        // Set the transformation method based on the visibility state
        if (isPasswordVisible) {
            // Show password
            transformationMethod = HideReturnsTransformationMethod.getInstance()
            clearButtonImage = ContextCompat.getDrawable(context, R.drawable.password_icon_visible) ?: return
        } else {
            // Hide password
            transformationMethod = PasswordTransformationMethod.getInstance()
            clearButtonImage = ContextCompat.getDrawable(context, R.drawable.password_icon_invisible) ?: return
        }

        // Update the drawable icon
        setCompoundDrawablesWithIntrinsicBounds(null, null, clearButtonImage, null)
    }
}
