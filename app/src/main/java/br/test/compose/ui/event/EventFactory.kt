package br.test.compose.ui.event

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.test.compose.ui.NavigationViewModel

class EventFactory(
    private val context: Context,
    private val navigationViewModel: NavigationViewModel,
    private val visibilityChange: (id: String, visible: Boolean) -> Unit) {

    @SuppressLint("ComposableNaming")
    @Composable
    fun build(event: Event): @Composable () -> Unit {
        return when (event.type) {
            EventType.CLICK -> buildClick(event)
            EventType.VALUE_CHANGE -> { { throw NotImplementedError() } }
        }
    }

    @Composable
    fun <T> buildForLiveData(event: Event, liveDataPool: HashMap<String, LiveData<Any>>, liveDataBuilder: () -> LiveData<T>): MutableState<T>? {
        return when (event.type) {
            EventType.CLICK -> throw NotImplementedError()
            EventType.VALUE_CHANGE -> buildValueChange(event, liveDataPool, liveDataBuilder)
        }
    }

    fun <T> updateLiveData(event: Event, liveDataPool: HashMap<String, LiveData<Any>>, value: T) {
        liveDataPool[event.bundle]?.let { ld ->
            @Suppress("UNCHECKED_CAST")
            (ld as MutableLiveData<T>).value = value
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
            EventActionType.LIVE_DATA -> {
                { throw NotImplementedError() }
            }
            EventActionType.TOAST -> {
                { Toast.makeText(context, event.bundle, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun <T> buildValueChange(event: Event, liveDataPool: HashMap<String, LiveData<Any>>, liveDataBuilder: () -> LiveData<T>): MutableState<T>? {
        return when (event.action) {
            EventActionType.ALERT -> throw NotImplementedError()
            EventActionType.START_SCREEN -> throw NotImplementedError()
            EventActionType.SET_INVISIBLE -> throw NotImplementedError()
            EventActionType.SET_VISIBLE -> throw NotImplementedError()
            EventActionType.LIVE_DATA -> {
                if (liveDataPool[event.bundle] == null) {
                    @Suppress("UNCHECKED_CAST")
                    liveDataPool[event.bundle] = liveDataBuilder.invoke() as LiveData<Any>
                }
                getStateFromLiveData(event, liveDataPool)
            }
            EventActionType.TOAST -> throw NotImplementedError()
            }
    }

    @Composable
    private fun <T> getStateFromLiveData(event: Event, liveDataPool: HashMap<String, LiveData<Any>>): MutableState<T>? {
        liveDataPool[event.bundle]?.let {
            @Suppress("UNCHECKED_CAST")
            return it.observeAsState() as MutableState<T>
        }
        return null
    }
}