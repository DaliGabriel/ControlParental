package com.example.contactos.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactos.model.Posts
import com.example.contactos.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository) : ViewModel() {

    val myResponse: MutableLiveData<Posts> = MutableLiveData()
    val myPushResponse: MutableLiveData<Response<Posts>> = MutableLiveData()

    fun getPosts() {
        viewModelScope.launch {
            val response = repository.getPosts()
            myResponse.value = response
        }
    }

    fun pushPosts(nombre: String,numero: String, modelo: String) {
        viewModelScope.launch {
            val response = repository.pushPosts(nombre, numero, modelo)
            myPushResponse.value = response
        }
    }



}