package com.example.team3

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.team3.databinding.FragmentMemoBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

class MemoFragment(var memoPath:String, val flag:Int, val date:String, val file_flag:Int) : Fragment() {
    var binding: FragmentMemoBinding?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMemoBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        binding!!.apply {

//            button.setOnClickListener {
//                var start = editMemo.selectionStart
//                var end = editMemo.selectionEnd
//
//                if(start == 0 && end == 0)
//                {
//                    start = editMemo.text.length - 1
//                    end = start + 1
//                }
//
//                editMemo.text.setSpan(BackgroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//            }

            mSaveBtn.setOnClickListener {

                var txtFile:File? = null
                when(flag){
                    AddMemo.NEWMEMO->{
                        txtFile = createTxtFile()
                    }
                    AddMemo.MODIFYTEXT->{
                        txtFile = File(memoPath)
                    }
                    in AddMemo.MODIFYPICTURE..AddMemo.MODIFYDRAWING ->{
                        val oldFile = File(memoPath)
                        oldFile.delete()
                        txtFile = createTxtFile()
                        memoPath = txtFile.absolutePath
                    }
                }

                val fout = FileOutputStream(txtFile!!)
                val writer = PrintWriter(fout)

                writer.println(editMemo.text.toString())
                writer.close()
                fout.close()

                Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
                Log.i("주소", "$memoPath")

                val resultIntent = Intent()
                resultIntent.putExtra("path", memoPath)
                resultIntent.putExtra("fileflag", file_flag+1)
                requireActivity().setResult(RESULT_OK, resultIntent)
                requireActivity().finish()
            }
        }
    }

    private fun init() {
        if(flag == AddMemo.MODIFYTEXT){
            val scan = Scanner(FileInputStream(memoPath))
            var myString:String = ""
            while(scan.hasNextLine()){
                myString += scan.nextLine() + "\n"
            }
            binding!!.editMemo.setText(myString)
            scan.close()
        }
    }

    fun createTxtFile(): File {
        val imageFileName = date + "_" + file_flag + "_txt_"
        val storageDir = requireActivity().getExternalFilesDir(null)
        //Prefix : imageFilename, Suffix : .jpg, Directory : storageDir
        val txtFile = File.createTempFile(imageFileName, ".txt", storageDir)
        memoPath = txtFile.absolutePath
        return txtFile
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}