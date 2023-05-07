package com.example.gralley

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.pager_photo_view.view.*
import java.lang.reflect.Type
import java.text.FieldPosition

class PagePhotoListAdapter:androidx.recyclerview.widget.ListAdapter<PhotoItem,PagerPhotoViewHolder>(GalleryAdapter.DIFFCALLBACK){
    object DiffCallback:DiffUtil.ItemCallback<PhotoItem>(){
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            TODO("Not yet implemented")
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerPhotoViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.pager_photo_view,parent,false).apply {
            return PagerPhotoViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: PagerPhotoViewHolder, position: Int) {
        Glide.with(holder.itemView).load(getItem(position).previewUrl).placeholder(R.drawable.photo_placeholder)
            .into(holder.itemView.pagePhoto)
    }

}
class PagerPhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)