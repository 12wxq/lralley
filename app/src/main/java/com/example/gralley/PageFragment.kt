package com.example.gralley

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_page.*
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val REQUEST_WRITE_EXTERNAL_STORAGE=1
class PageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val photoList=arguments?.getParcelableArrayList<PhotoItem>("PHOTO_LIST")
        PagePhotoListAdapter().apply {
            viewPager2.adapter= this
            submitList(photoList)
        }
        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                photoTag.text=getString(R.string.photo_tag,position+1,photoList?.size)
                //photoTag.text="${position+1}/${photoList?.size}"
            }
        })
        viewPager2.setCurrentItem(arguments?.getInt("PHOTO_POSITION")?:0,false)
        viewPager2.orientation=ViewPager2.ORIENTATION_HORIZONTAL
        savebutton.setOnClickListener {
            if(Build.VERSION.SDK_INT<29&&ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE)
            }else{
                viewLifecycleOwner.lifecycleScope.launch {
                    savePhoto()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_WRITE_EXTERNAL_STORAGE->{
                if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   viewLifecycleOwner.lifecycleScope.launch {
                       savePhoto()
                   }

                }else{
                    Toast.makeText(requireContext(),"没有权限，存储失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private suspend fun savePhoto(){//suspend 允许挂起//线程
        withContext(Dispatchers.IO){
            val  holder=(viewPager2[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager2.currentItem) as PagerPhotoViewHolder
            val bitmap = holder.itemView.pagePhoto.drawable.toBitmap()
            val saveUri=requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )?: kotlin.run {
             MainScope().launch { Toast.makeText(requireContext(),"存储失败",Toast.LENGTH_SHORT).show() }
                return@withContext
            }
            requireContext().contentResolver.openOutputStream(saveUri).use {
                if( bitmap.compress(Bitmap.CompressFormat.JPEG,90,it)){
                    MainScope().launch { Toast.makeText(requireContext(),"存储成功",Toast.LENGTH_SHORT).show() }
                }else{
                    MainScope().launch { Toast.makeText(requireContext(),"存储失败",Toast.LENGTH_SHORT).show() }
                }
            }
        }


//      if (  MediaStore.Images.Media.insertImage(requireContext().contentResolver,bitmap,"","")==null)
//      {
//          Toast.makeText(requireContext(),"保存失败",Toast.LENGTH_SHORT).show()
//      }  else{
//        Toast.makeText(requireContext(),"保存成功",Toast.LENGTH_SHORT).show()}
  }
}