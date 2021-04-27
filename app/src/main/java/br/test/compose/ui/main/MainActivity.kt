package br.test.compose.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.test.compose.App
import br.test.compose.ui.NavigationViewModel
import br.test.compose.ui.ResourceProvider

class MainActivity : AppCompatActivity() {

    private lateinit var navigationViewModel: NavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceProvider.resource = resources
        navigationViewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)

        setContent {
            App(this@MainActivity, navigationViewModel)
        }

        navigationViewModel.screens.observe(this, {
            Toast.makeText(this, "Screens loaded", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onBackPressed() {
        if (!navigationViewModel.goBack()) {
            super.onBackPressed()
        }
    }
}