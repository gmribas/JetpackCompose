package br.test.compose.ui

import android.content.Context
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import br.test.compose.ui.factory.ScreenFactory
import br.test.compose.ui.widget.Widget
import br.test.compose.ui.theme.Theme

/**
 *
 * Created by gmribas on 19/04/21.
 */
@Composable
fun App(context: Context, widget: Widget) {
    Theme {
        AppContent(context, widget)
    }
}

@Composable
private fun AppContent(context: Context, widget: Widget) {
    Surface(color = MaterialTheme.colors.background) {
        ScreenFactory(context).Build(widget)
    }
}