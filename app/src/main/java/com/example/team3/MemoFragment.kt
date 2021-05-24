package com.example.team3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.team3.databinding.FragmentMemoBinding
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*


class MemoFragment : Fragment() {
    var binding: FragmentMemoBinding?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMemoBinding.inflate(layoutInflater)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                val txtFile = createTxtFile()
                val fout = FileOutputStream(txtFile)
                val writer = PrintWriter(fout)

                writer.println(editMemo.text.toString())
                writer.close()
                fout.close()

                Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
            }

        }

    }

    fun createTxtFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd").format(Date())
        val imageFileName = timeStamp + "_"

        val storageDir = requireActivity().getExternalFilesDir(null)
        //Prefix : imageFilename, Suffix : .jpg, Directory : storageDir
        val txtFile = File.createTempFile(imageFileName, ".txt", storageDir)

        return txtFile
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}