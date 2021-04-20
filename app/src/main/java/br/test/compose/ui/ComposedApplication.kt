package br.test.compose.ui

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import br.test.compose.R
import br.test.compose.ui.factory.ScreenFactory
import br.test.compose.ui.parser.Widget
import br.test.compose.ui.theme.Theme
import com.google.gson.Gson
import org.apache.commons.io.IOUtils

/**
 *
 * Created by gmribas on 19/04/21.
 */
@Composable
fun App(context: Context) {
    Theme {
        AppContent(context)
    }
}

@Composable
private fun AppContent(context: Context) {
    Surface(color = MaterialTheme.colors.background) {
        val input = context.resources.openRawResource(R.raw.content)
        @Suppress("DEPRECATION")
        val rawJson = IOUtils.toString(input)
        input.close()
        val widget = Gson().fromJson(rawJson, Widget::class.java)
        ScreenFactory(context).Build(widget)
    }
}