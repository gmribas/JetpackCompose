package br.test.compose.ui

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.test.compose.R
import br.test.compose.ui.screen.Screen
import br.test.compose.ui.widget.Widget
import br.test.compose.ui.widget.WidgetViewClassType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class NavigationViewModel: ViewModel(), CoroutineScope {

    private val screens = arrayListOf<Screen>()
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

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    fun buildScreens(context: Context) = launch {
        @Suppress("UNCHECKED_CAST")
        withContext(Dispatchers.Default) {
            val input = context.resources.openRawResource(R.raw.content)
            @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
            val rawJson = IOUtils.toString(input)
            @Suppress("BlockingMethodInNonBlockingContext")
            input.close()

            val type = object : TypeToken<ArrayList<Screen>>() {}.type
            val screens = Gson().fromJson(rawJson, type) as List<Screen>
            this@NavigationViewModel.screens.clear()
            this@NavigationViewModel.screens.addAll(screens)
        }

        val first = screens.first { it.first ?: false }
        screenStack.addLast(first)
        currentScreen.value = first
    }

    fun goToScreen(screenId: String) = launch {
        doGoToScreen(screenId)
    }

    private fun doGoToScreen(screenId: String, addToStack: Boolean = true) = launch {
        screens.firstOrNull { it.id == screenId }?.let {
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