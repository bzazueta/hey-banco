package com.sycnos.heyvisitas.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sycnos.heyvisitas.model.Model
import com.sycnos.heyvisitas.model.Provider

class ViewModel : ViewModel() {
    val quoteModel = MutableLiveData<Model>()
    fun randomQuote() {
        val currentQuote = Provider.random()
        quoteModel.postValue(currentQuote)
    }
}