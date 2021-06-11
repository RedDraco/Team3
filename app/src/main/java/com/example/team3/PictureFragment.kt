package com.example.team3

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.team3.databinding.FragmentPictureBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PictureFragment(var memoPath:String, val flag:Int, val date:String, val file_flag:Int) : Fragment() {

    var binding: FragmentPictureBinding?= null
    val IMAGECAP = 10
    val IMAGEGAL = 11
    var currentPhotoPath:String = ""//
    var outBitmap:Bitmap? = null
    val tempURI = arrayListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPictureBinding.inflate(layoutInflater)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        binding!!.apply {

            cameraBtn.setOnClickListener{
                val photoFile = createImageFile() //이미지 파일을 생성하는 커스텀 메서드

                if(photoFile != null){
                    val photoURI = FileProvider.getUriForFile(context!!, "com.example.team3.fileProvider", photoFile)
                    tempURI.add(photoURI)

                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI) // Key : MediaStore.EXTRA_OUTPUT
                    startActivityForResult(cameraIntent, IMAGECAP)
                }
            }

            galleryBtn.setOnClickListener {

                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.type = "image/*"
                startActivityForResult(galleryIntent, IMAGEGAL)

            }

            pSaveBtn.setOnClickListener {
                Log.i("flag", "$flag")
                var photoFile:File? = null
                when(flag){
                    AddMemo.NEWMEMO->{
                        photoFile = createImageFile()
                        memoPath = photoFile.absolutePath
                    }
                    AddMemo.MODIFYTEXT->{
                        val oldFile = File(memoPath)
                        oldFile.delete()
                        photoFile = createImageFile()
                        memoPath = photoFile.absolutePath
                    }
                    AddMemo.MODIFYPICTURE->{
                        photoFile = File(memoPath)
                        memoPath = photoFile.absolutePath
                    }
                    AddMemo.MODIFYDRAWING ->{
                        val oldFile = File(memoPath)
                        oldFile.delete()
                        photoFile = createImageFile()
                        memoPath = photoFile.absolutePath
                    }
                }
                if(outBitmap != null){
                    val fout = FileOutputStream(photoFile)
                    outBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fout)
                    fout.close()
                    Toast.makeText(requireContext(), "저장 완료", Toast.LENGTH_SHORT).show()
                    Log.i("주소", "$memoPath")
                    Log.i("주소", "memoPath")
                }
                Log.i("주소", "주소 : $memoPath")
                val resultIntent = Intent()
                resultIntent.putExtra("path", memoPath)
                resultIntent.putExtra("fileflag", file_flag+1)
                requireActivity().setResult(RESULT_OK, resultIntent)
                requireActivity().finish()
            }
        }
    }   

    private fun init() {
        if(flag == AddMemo.MODIFYPICTURE){

            val jpgFile = File(memoPath) //파일 경로 -> File 객체
            val photoURI = jpgFile.toUri() //파일 객체 -> uri 객체
            val sourceBitmap = ImageDecoder.createSource(requireActivity().contentResolver, photoURI) //uri 객체 -> ImageDecoder.Source 객체
            val tempBitmap = ImageDecoder.decodeBitmap(sourceBitmap) //ImageDecoder.Source 객체 -> Bitmap 객체
            val newBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true) //Read-Only Bitmap 객체 -> 복사 -> Writable Bitmap 객체
            binding!!.uploadImage.setImageBitmap(newBitmap)
            outBitmap = newBitmap
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            IMAGECAP ->{
                val file = File(currentPhotoPath)
                val photoURI = FileProvider.getUriForFile(context!!, "com.example.team3.fileProvider", file)

                if(resultCode == RESULT_OK){
                    val sourceBitmap = ImageDecoder.createSource(requireActivity().contentResolver, photoURI)
                    val bitmap = ImageDecoder.decodeBitmap(sourceBitmap)

                    if(bitmap != null){
                        binding!!.uploadImage.setImageBitmap(bitmap)
                        outBitmap = bitmap
                    }
                }
            }

            IMAGEGAL ->{
                if(resultCode == RESULT_OK){
                    val sourceBitmap = ImageDecoder.createSource(requireActivity().contentResolver, data!!.data!!)
                    val bitmap = ImageDecoder.decodeBitmap(sourceBitmap)

                    if(bitmap != null){
                        binding!!.uploadImage.setImageBitmap(bitmap)
                        outBitmap = bitmap
                    }
                }
            }
        }
    }

    fun createImageFile():File {
        val imageFileName = date + "_" + file_flag + "_jpg_"

        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //Prefix : imageFilename, Suffix : .jpg, Directory : storageDir
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = imageFile.absolutePath

        return imageFile
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null

        for(tempURI in tempURI){
            context!!.contentResolver.delete(tempURI, null, null)
        }
    }
}