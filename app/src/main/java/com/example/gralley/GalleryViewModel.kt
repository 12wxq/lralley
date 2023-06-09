package com.example.gralley

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlin.math.ceil
const val DATA_STATUS_CAN_MODE=0
const val DATA_STATUS_NO_MODE=1
const val DATA_STATUS_NETWORK_MODE=2

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val  _dataStatusLive=MutableLiveData<Int>()
    val dataStateslive:LiveData<Int> get()=_dataStatusLive
    private val _photoListLive= MutableLiveData<List<PhotoItem>>()
    val photoListLive :LiveData<List<PhotoItem>>
    get() = _photoListLive
    var needToScrollToTop=true
    private val perPage=50
    private var currentPage=1
    private var totalPage=1
    private var currentKey="cat"
    private var isNewQuery=true
    private var isLoading=false
    private val keyWords= arrayOf("car","dog","snake","phone","flower","son","animal")
      init {
    resetQuery()
}

    fun resetQuery(){
        currentPage=1
        totalPage=1
        currentKey=keyWords.random()
        isNewQuery=true
        needToScrollToTop=true
        fetchData()
    }
    fun fetchData(){
        if (isLoading) return

        if (currentPage>totalPage) {
            _dataStatusLive.value= DATA_STATUS_NO_MODE
            return
        }
        isLoading=true
        val stringRequest: StringRequest =StringRequest(
            Request.Method.GET,
            getUrl(),
            Response.Listener {
                with(Gson().fromJson(it,Pixabay::class.java)){
                    totalPage= ceil(totalHits.toDouble()/perPage).toInt()
                    if (isNewQuery){
                        _photoListLive.value=hits.toList()
                    }else{
                        _photoListLive.value= arrayListOf(_photoListLive.value!!,hits.toList()).flatten()
                    }
                }
                _dataStatusLive.value= DATA_STATUS_CAN_MODE
                isLoading=false
                isNewQuery=false
                currentPage++
             // _photoListLive.value=Gson().fromJson(it,Pixabay::class.java).hits.toList()

            },
            Response.ErrorListener {
                _dataStatusLive.value= DATA_STATUS_NETWORK_MODE
                isLoading=false

            }



        )
        VolleySingleton.getInstance(getApplication()).requestQueue.add(stringRequest)
    }
    private fun getUrl():String{
        return "https://pixabay.com/api/?key=35849937-cd7b4fabaf69c60543eeea592&q=${currentKey}&per_page=${perPage}&page=${currentPage}"
    }

}