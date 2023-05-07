package com.example.gralley

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_gallery.*

//// TODO: Rename parameter arguments, choose names that match
//// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
//
///**
// * A simple [Fragment] subclass.
// * Use the [GalleryFragment.newInstance] factory method to
// * create an instance of this fragment.
// */
class GalleryFragment : Fragment() {
  private lateinit var galleryViewModel: GalleryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.swipeind->{
                swiperLayoutGallery.isRefreshing=true
                Handler().postDelayed(Runnable {
                    galleryViewModel.resetQuery()
                },1000)

            }



        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu,menu)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        galleryViewModel=ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(GalleryViewModel::class.java)
        val galleryAdapter=GalleryAdapter(galleryViewModel)
        recyclerview.apply {
            adapter=galleryAdapter
            layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        }

        galleryViewModel.photoListLive.observe(viewLifecycleOwner, Observer {
            if (galleryViewModel.needToScrollToTop){
                recyclerview.scrollToPosition(0)
                galleryViewModel.needToScrollToTop=false
            }
            galleryAdapter.submitList(it)
            swiperLayoutGallery.isRefreshing=false//停止转动
        })
        galleryViewModel.dataStateslive.observe(viewLifecycleOwner, Observer {
            galleryAdapter.footerViewStaus=it
            galleryAdapter.notifyItemChanged(galleryAdapter.itemCount-1)
            if (it== DATA_STATUS_NETWORK_MODE)swiperLayoutGallery.isRefreshing=false

        }
        )
       // galleryViewModel.photoListLive.value?:galleryViewModel.resetQuery()
        swiperLayoutGallery.setOnRefreshListener {
            galleryViewModel.resetQuery()//下拉刷新图片
        }
        recyclerview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy<0) return
                val  layoutmanager=recyclerView.layoutManager as StaggeredGridLayoutManager
                val intArray=IntArray(2)
                layoutmanager.findLastVisibleItemPositions(intArray)
                if (intArray[0]==galleryAdapter.itemCount-1){
                       galleryViewModel.fetchData()//加载数据
                }

            }
        })


    }


}