package com.example.driverattentiveness.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.example.driverattentiveness.R

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) { // Ganti menjadi AppCompatEditText

    private var doneIcon: Drawable? = null

    init {
        // Menetapkan icon done pada drawableEnd
        doneIcon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_done_24, null) // Gunakan ResourcesCompat
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, doneIcon, null)

        // Memantau perubahan teks
        addTextChangedListener {
            validateEmail()
        }
    }

    private fun validateEmail() {
        // Cek apakah teks yang dimasukkan merupakan email yang valid
        val isValidEmail = !TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches()

        if (isValidEmail) {
            // Jika email valid, tampilkan icon done
            doneIcon?.let {
                setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, it, null)
            }
        } else {
            // Jika email tidak valid, hilangkan icon done
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
        }
    }
}