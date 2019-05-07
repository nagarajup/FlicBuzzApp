package com.aniapps.flicbuzzapp.utils

import android.graphics.Color
import android.text.style.ClickableSpan
import android.view.View
import android.text.TextPaint





open class MySpannable(b: Boolean) : ClickableSpan() {
    private var isUnderline = false
    override fun onClick(p0: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun MySpannable(isUnderline: Boolean) {
        this.isUnderline = isUnderline
    }

    override fun updateDrawState(ds: TextPaint) {

        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor("#00ff00")

    }
}