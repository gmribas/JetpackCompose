package br.test.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.test.compose.ui.App
import br.test.compose.ui.MainViewModel
import br.test.compose.ui.screen.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.firstWidget.observe(this, { onLoadScreens(it) })
        viewModel.buildScreens(this)
    }

    private fun onLoadScreens(firstScreen: Screen) {
        setContent {
            App(this@MainActivity, firstScreen.widget)
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}