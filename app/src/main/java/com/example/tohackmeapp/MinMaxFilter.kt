package com.example.tohackmeapp

import android.text.InputFilter
import android.text.Spanned

class MinMaxFilter(private val minVal:Int, private val maxVal:Int) : InputFilter {

    override fun filter(
        p0: CharSequence?,
        p1: Int,
        p2: Int,
        p3: Spanned?,
        p4: Int,
        p5: Int
    ): CharSequence? {
        val input = (p3.toString() + p0.toString()).toInt()
        if (isInRange(minVal, maxVal, input)) return null
        return ""
    }
    private fun isInRange (a: Int, b: Int, c: Int): Boolean {
        return if (b > a){
            (c >= a) && (c <= b)
        }else{
            (c >= b) && (c <= a)
        }
    }

}