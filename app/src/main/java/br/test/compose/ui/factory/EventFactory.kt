package br.test.compose.ui.factory

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.test.compose.ui.NavigationViewModel
import br.test.compose.ui.event.Event
import br.test.compose.ui.event.EventActionType
import br.test.compose.ui.event.EventType

class EventFactory(
    private val context: Context,
    private val navigationViewModel: NavigationViewModel,
    private val visibilityChange: (id: String, visible: Boolean) -> Unit
    ) {

    @SuppressLint("ComposableNaming")
    @Composable
    fun build(event: Event): @Composable () -> Unit {
        return when (event.type) {
            EventType.CLICK -> buildClick(event)
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun buildClick(event: Event): @Composable () -> Unit {
        val showAlert = remember { mutableStateOf(false) }
        if (showAlert.value) {
            AlertDialog(
                title = {
                    Column {
                        Text(text = event.bundle)
                    }
                },
                buttons = {
                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(0.dp, 0.dp, 10.dp, 10.dp)
                    ) {
                        Button(onClick = { showAlert.value = false }) {
                            Text(text = "Dismiss")
                        }
                    }
                },
                onDismissRequest = { },
                modifier = Modifier
                    .height(200.dp)
                    .width(400.dp)
            )
        }

        return when (event.action) {
            EventActionType.ALERT -> {
                { showAlert.value = true }
            }
            EventActionType.START_SCREEN -> {
                { navigationViewModel.goToScreen(event.bundle) }
            }
            EventActionType.SET_INVISIBLE -> {
                { visibilityChange(event.bundle, false) }
            }
            EventActionType.SET_VISIBLE -> {
                { visibilityChange(event.bundle, true) }
            }
            EventActionType.TOAST -> {
                { Toast.makeText(context, event.bundle, Toast.LENGTH_SHORT).show() }
            }
        }
    }
}