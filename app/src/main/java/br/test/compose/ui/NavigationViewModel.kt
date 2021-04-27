package br.test.compose.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import br.test.compose.ui.screen.Screen
import br.test.compose.ui.widget.Widget
import br.test.compose.ui.widget.WidgetViewClassType
import kotlinx.coroutines.Dispatchers
import java.util.*

class NavigationViewModel: ViewModel() {

    private val _screens: MutableLiveData<List<Screen>> = JsonScreenReader
        .read()
        .asLiveData(Dispatchers.Default + viewModelScope.coroutineContext, 500)
        .map { screenList ->
            val first = screenList.first { it.first ?: false }
            screenStack.addLast(first)
            currentScreen.value = first
            screenList
        } as MutableLiveData<List<Screen>>

    val screens = _screens

    private val screenStack = LinkedList<Screen>()
    val currentScreen = mutableStateOf(
        Screen(
            "id",
            null,
            Widget(
                id = "id",
                viewClass = WidgetViewClassType.EMPTY_VIEW,
                event = null
            )
        )
    )

    fun goToScreen(screenId: String) {
        doGoToScreen(screenId)
    }

    private fun doGoToScreen(screenId: String, addToStack: Boolean = true) {
        screens.value?.firstOrNull { it.id == screenId }?.let {
            if (addToStack) {
                screenStack.addLast(it)
            }
            currentScreen.value = it
        }
    }

    fun goBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeLast()
            doGoToScreen(screenStack.last.id, false)
            return true
        }
        return false
    }
}