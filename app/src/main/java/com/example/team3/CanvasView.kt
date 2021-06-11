package com.example.team3

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

class CanvasView(context: Context?) : View(context) {

    companion object{
        const val ERASER:Int = 0
        const val NORMAL:Int = 1
        const val CIRCLE:Int = 2
    }
    var myStrokeSize = 10f
    val colorList = arrayListOf<Int>()
    var colorNum = 0

    val paint = Paint()
    val arrayPath = arrayListOf<CustomPath>()

    var myBitmap:Bitmap? = null
    var typeFlag:Int = NORMAL

    init{
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.isAntiAlias = true
        paint.strokeWidth = 10f

        colorList.add(Color.BLACK)
        colorList.add(Color.RED)
        colorList.add(Color.GREEN)
        colorList.add(Color.BLUE)
        colorList.add(Color.CYAN)
        colorList.add(Color.MAGENTA)
        colorList.add(Color.YELLOW)
    }

    fun loadBitmap(bitmap: Bitmap){
        myBitmap = bitmap
    }

    fun changePen(mode:Int):Int{
        when(mode){
            NORMAL->{
                colorNum++
                if(colorNum >= colorList.size)
                    colorNum = 0
                paint.color = colorList[colorNum]
                typeFlag = NORMAL
            }
            ERASER->{
                colorNum = -1
                paint.color = Color.WHITE
                typeFlag = ERASER
            }
            CIRCLE->{
                typeFlag = CIRCLE
            }
        }
        return paint.color
    }

    fun changeStrokeSize(isUp:Boolean):Float{
        if(isUp){
            myStrokeSize += 5f
            if(myStrokeSize > 50f)
                myStrokeSize = 50f
            paint.strokeWidth = myStrokeSize
            return paint.strokeWidth
        }
        else{
            myStrokeSize -= 5f
            if(myStrokeSize < 5f)
                myStrokeSize = 5f
            paint.strokeWidth = myStrokeSize
            return paint.strokeWidth
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

                when(typeFlag){
                    NORMAL->{
                        val path = arrayPath.last().path
                        path.lineTo(event.x, event.y)

                        invalidate()
                    }
                    ERASER->{
                        val path = arrayPath.last().path
                        path.lineTo(event.x, event.y)

                        invalidate()
                    }
                }
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