package com.example.team3

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

class CanvasView(context: Context?) : View(context) {

    companion object{
        const val ERASER:Int = 0
        const val NORMAL_BLACK:Int = 1
    }

    val paint = Paint()
    val arrayPath = arrayListOf<CustomPath>()
    var myBitmap:Bitmap? = null

    init{
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.isAntiAlias = true
        paint.strokeWidth = 10f
    }

    fun loadBitmap(bitmap: Bitmap){
        myBitmap = bitmap
    }

    fun changePen(mode:Int){
        when(mode){
            NORMAL_BLACK->{
                paint.color = Color.BLACK
                paint.strokeWidth = 10f
            }
            ERASER->{
                paint.color = Color.WHITE
                paint.strokeWidth = 30f
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event == null)
            return false

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                val path = Path()
                path.moveTo(event.x, event.y)

                val customPath = CustomPath(path, paint.color, paint.strokeWidth)
                arrayPath.add(customPath)
            }

            MotionEvent.ACTION_MOVE -> {
                val path = arrayPath.last().path
                path.lineTo(event.x, event.y)

                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                invalidate()
            }
            else -> {}
        }
        return true
    }


    override fun onDraw(canvas: Canvas?) {

        if(myBitmap != null)
            canvas?.drawBitmap(myBitmap!!, 0f, 0f, null)

        for(path in arrayPath){
            paint.color = path.color
            paint.strokeWidth = path.width
            canvas?.drawPath(path.path, paint)
        }

        super.onDraw(canvas)
    }
}