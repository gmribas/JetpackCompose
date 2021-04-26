package br.test.compose

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import br.test.compose.ui.NavigationViewModel
import br.test.compose.ui.factory.ScreenFactory
import br.test.compose.ui.theme.Theme

/**
 *
 * Created by gmribas on 19/04/21.
 */
@Composable
fun App(context: Context, navigationViewModel: NavigationViewModel) {
    Theme {
        AppContent(context, navigationViewModel)
    }
}

@Composable
private fun AppContent(context: Context, navigationViewModel: NavigationViewModel) {
    Crossfade(navigationViewModel.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            ScreenFactory(context, navigationViewModel).Build(screen.value.widget)
        }
    }
}