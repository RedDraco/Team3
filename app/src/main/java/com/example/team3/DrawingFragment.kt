package com.example.team3

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.team3.databinding.FragmentDrawingBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class DrawingFragment : Fragment() {

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
        currentCanvasView = replaceCanvasView(null)

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

                    val jpgFile = createImageFile()
                    val fout = FileOutputStream(jpgFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout)
                    fout.close()

                    Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd").format(Date())
        val imageFileName = timeStamp + "_"

        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //Prefix : imageFilename, Suffix : .jpg, Directory : storageDir
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

        return imageFile
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}