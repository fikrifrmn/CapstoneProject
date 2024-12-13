package com.dicoding.intermediatefirst.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.driverattentiveness.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) validateEmail() else hideErrorAndIcon()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun validateEmail() {
        val inputText = text.toString().trim()
        val drawable = compoundDrawables[2] // Access the drawableEnd (icon at the end of the EditText)

        when {
            inputText.isEmpty() -> {
                hideErrorAndIcon()
            }
            !Patterns.EMAIL_ADDRESS.matcher(inputText).matches() -> {
                hideErrorAndIcon()
            }
            else -> {
                if (drawable != null) {
                    drawable.setColorFilter(context.getColor(R.color.green), android.graphics.PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }

    private fun hideErrorAndIcon() {
        val drawable = compoundDrawables[2]
        drawable?.clearColorFilter()
    }
}
