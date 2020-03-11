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
val scGap : Float = 0.02f
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

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class LTEBNode(var i : Int, val state : State = State()) {

        private var next : LTEBNode? = null
        private var prev : LTEBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            next = LTEBNode(i + 1)
            next?.prev = this
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBLENode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LTEBNode {
            var curr : LTEBNode? = prev
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LineToEBouncy(var i : Int) {

        private val root : LTEBNode = LTEBNode(0)
        private var curr : LTEBNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }
}