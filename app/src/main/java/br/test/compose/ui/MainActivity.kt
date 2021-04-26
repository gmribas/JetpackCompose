package br.test.compose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.test.compose.App

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val navigationViewModel = NavigationViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContent {
            App(this@MainActivity, navigationViewModel)
        }

        navigationViewModel.buildScreens(this)
    }

    override fun onBackPressed() {
        if (!navigationViewModel.goBack()) {
            super.onBackPressed()
        }
    }
}