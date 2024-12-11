package com.example.driverattentiveness.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
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
        hint = "Password must at least be 8 length"
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

        // Set the password toggle icon
        setDrawableEndIcon()

        // Set up the click listener for the password visibility toggle
        setOnTouchListener { _, event ->
            if (compoundDrawables[2] != null && event.action == android.view.MotionEvent.ACTION_UP) {
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
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.password_icon) ?: return
        setCompoundDrawablesWithIntrinsicBounds(null, null, clearButtonImage, null)
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            transformationMethod = PasswordTransformationMethod.getInstance() // Hide password
            clearButtonImage = ContextCompat.getDrawable(context, R.drawable.password_icon) ?: return
        } else {
            transformationMethod = HideReturnsTransformationMethod.getInstance() // Show password
            clearButtonImage = ContextCompat.getDrawable(context, R.drawable.password_icon) ?: return
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null, clearButtonImage, null)
        isPasswordVisible = !isPasswordVisible
    }

}