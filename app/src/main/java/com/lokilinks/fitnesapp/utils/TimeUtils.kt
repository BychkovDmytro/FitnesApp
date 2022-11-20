package com.lokilinks.fitnesapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar

object TimeUtils {
    @SuppressLint("SimpleDateFormat")
    val formater = SimpleDateFormat("mm:ss")

    fun getTime(time: Long): String{
        val cv = Calendar.getInstance()
        cv.timeInMillis = time
        return formater.format(cv.time)
    }
}