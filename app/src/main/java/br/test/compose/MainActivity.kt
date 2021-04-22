package br.test.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import br.test.compose.ui.App
import br.test.compose.ui.MainViewModel
import br.test.compose.ui.widget.Widget
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        buildWidget()
    }

    private fun buildWidget() = launch {
        val widget = withContext(Dispatchers.Default) {
            val input = resources.openRawResource(R.raw.content)
            @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
            val rawJson = IOUtils.toString(input)
            @Suppress("BlockingMethodInNonBlockingContext")
            input.close()
            Gson().fromJson(rawJson, Widget::class.java)
        }

        setContent {
            App(applicationContext, widget)
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main
}