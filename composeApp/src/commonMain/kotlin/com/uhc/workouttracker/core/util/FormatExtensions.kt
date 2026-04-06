package com.uhc.workouttracker.core.util

// KMP-safe float formatting (String.format with %f is not available on iOS/Native)
internal fun Float.format1d(): String {
    val scaled = (this * 10 + 0.5f).toInt()
    return "${scaled / 10}.${scaled % 10}"
}
internal fun Float.format2d(): String = this.toDouble().format2d()
internal fun Double.format2d(): String {
    val scaled = (this * 100 + 0.5).toLong()
    return "${scaled / 100}.${(scaled % 100).toString().padStart(2, '0')}"
}
