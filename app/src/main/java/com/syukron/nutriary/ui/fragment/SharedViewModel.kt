package com.syukron.nutriary.ui.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val predictionResult = MutableLiveData<String>()
}