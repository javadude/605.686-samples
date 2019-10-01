package com.javadude.customviews

import android.graphics.RectF

class Thing(val type : Type, var bounds : RectF) {
    enum class Type {
        Square, Circle, Triangle
    }
}