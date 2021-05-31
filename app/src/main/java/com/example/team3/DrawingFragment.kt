package com.example.team3

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.team3.databinding.FragmentDrawingBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class DrawingFragment(var memoPath:String, val flag:Int) : Fragment() {

    var binding: FragmentDrawingBinding?= null
    var currentCanvasView : CanvasView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDrawingBinding.inflate(layoutInflater)
        return binding!!.root
    }

    fun replaceCanvasView(canvasView: CanvasView?) : CanvasView{

        binding!!.apply {

            canvasFrame.removeAllViews()
            if(canvasView == null){
                val canvasViewR = CanvasView(activity)
                val viewParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1
                )
                canvasViewR.layoutParams = viewParams
                canvasViewR.setBackgroundColor(Color.WHITE)
                canvasFrame.addView(canvasViewR)
                return canvasViewR
            }
            else{
                canvasFrame.addView(canvasView)
                return canvasView
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!init()){
            currentCanvasView = replaceCanvasView(null)
        }

        binding!!.apply {

            penBtn.setOnClickListener{
                currentCanvasView?.changePen(CanvasView.NORMAL_BLACK)
            }

            eraserBtn.setOnClickListener{
                currentCanvasView?.changePen(CanvasView.ERASER)
            }

            refreshBtn.setOnClickListener {
                currentCanvasView = replaceCanvasView(null)
            }

            dSaveBtn.setOnClickListener {
                Log.i("flag", "$flag")
                var drawingFile:File? = null
                when(flag){
                    AddMemo.NEWMEMO->{
                        drawingFile = createImageFile()
                    }
                    AddMemo.MODIFYTEXT->{
                        val oldFile = File(memoPath)
                        oldFile.delete()
                        drawingFile = createImageFile()
                        memoPath = drawingFile.absolutePath
                    }
                    AddMemo.MODIFYPICTURE->{
                        val oldFile = File(memoPath)
                        oldFile.delete()
                        drawingFile = createImageFile()
                        memoPath = drawingFile.absolutePath
                    }
                    AddMemo.MODIFYDRAWING -> {
                        drawingFile = File(memoPath)
                    }
                }

                if(currentCanvasView != null){
                    val bitmap = Bitmap.createBitmap(currentCanvasView!!.width, currentCanvasView!!.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap) //생성한 비트맵 객체를 캔버스로...

                    //배경이 존재하면, 배경 먼저 그리기
                    if(currentCanvasView?.background != null){
                        currentCanvasView?.background!!.draw(canvas)
                    }
                    //배경이 없으면, 전부 흰색으로 먼저 그리기
                    else{
                        canvas.drawColor(Color.WHITE)
                    }
                    currentCanvasView?.draw(canvas) //사용자가 그린 그림 그리기

                    val fout = FileOutputStream(drawingFile)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout)
                    fout.close()
                    Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
                    Log.i("주소", "$memoPath")
                }
                val resultIntent = Intent()
                resultIntent.putExtra("path", memoPath)
                requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                requireActivity().finish()
            }
        }
    }

    private fun init():Boolean {
        if(flag == AddMemo.MODIFYDRAWING){

            val pngFile = File(memoPath) //파일 경로 -> File 객체
            val photoURI = FileProvider.getUriForFile(context!!, "com.example.team3.fileProvider", pngFile) //파일 객체 -> uri 객체
            val sourceBitmap = ImageDecoder.createSource(requireActivity().contentResolver, photoURI) //uri 객체 -> ImageDecoder.Source 객체
            val tempBitmap = ImageDecoder.decodeBitmap(sourceBitmap) //ImageDecoder.Source 객체 -> Bitmap 객체
            val newBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true) //Read-Only Bitmap 객체 -> 복사 -> Writable Bitmap 객체

            val newCanvas = CanvasView(activity)
            newCanvas.loadBitmap(newBitmap)
            currentCanvasView = replaceCanvasView(newCanvas)
            return true
        }
        return false
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd").format(Date())
        val imageFileName = timeStamp + "_"

        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //Prefix : imageFilename, Suffix : .jpg, Directory : storageDir
        val imageFile = File.createTempFile(imageFileName, ".png", storageDir)
        return imageFile
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}