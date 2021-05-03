package br.test.compose.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    val liveDataPool = hashMapOf<String, LiveData<Any>>()
}