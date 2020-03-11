package com.anwesh.uiprojects.linetoebouncyview

/**
 * Created by anweshmishra on 11/03/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val lines : Int = 5
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBouncyLineToE(scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    drawLine(0f, -size, 0f, size, paint)
    drawLine(0f, 0f, size / 2, 0f, paint)
    for (j in 0..1) {
        val sfi : Float = sf.divideScale(j, lines)
        save()
        scale(1f, 1f - 2 * j)
        drawLine(0f, 0f, size / 2 + (size / 2) * sfi, size * sfi, paint)
        restore()
    }
}

fun Canvas.drawBLENode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(w / 2, gap * (i + 1))
    drawBouncyLineToE(scale, gap / sizeFactor, paint)
    restore()
}

class LineToEBouncyView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}