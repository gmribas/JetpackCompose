package br.test.compose

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import br.test.compose.ui.NavigationViewModel
import br.test.compose.ui.screen.ScreenFactory
import br.test.compose.ui.theme.Theme

/**
 *
 * Created by gmribas on 19/04/21.
 */
@Composable
fun App(context: Context, navigationViewModel: NavigationViewModel, liveDataPool: HashMap<String, LiveData<Any>>) {
    Theme {
        AppContent(context, navigationViewModel, liveDataPool)
    }
}

@Composable
private fun AppContent(context: Context, navigationViewModel: NavigationViewModel, liveDataPool: HashMap<String, LiveData<Any>>) {
    Crossfade(navigationViewModel.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            ScreenFactory(context, navigationViewModel, liveDataPool).Build(screen.value.widget)
        }
    }
}