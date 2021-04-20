package br.test.compose.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import br.test.compose.ui.factory.ScreenFactory
import br.test.compose.ui.theme.Theme

/**
 *
 * Created by gmribas on 19/04/21.
 */
@Composable
fun App() {
    Theme {
        AppContent()
    }
}

@Composable
private fun AppContent() {
    Surface(color = MaterialTheme.colors.background) {
        ScreenFactory.build()
    }
}