package br.test.compose.ui

import android.content.Context
import androidx.activity.compose.setContent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.test.compose.R
import br.test.compose.ui.screen.Screen
import br.test.compose.ui.widget.Widget
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.IOUtils
import kotlin.coroutines.CoroutineContext
import com.google.gson.reflect.TypeToken




class MainViewModel : ViewModel(), CoroutineScope {

    private val _screens = MutableLiveData<List<Screen>>()
    val screens: LiveData<List<Screen>> = _screens
    private val _firstWidget = MutableLiveData<Screen>()
    val firstWidget: LiveData<Screen> = _firstWidget

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    fun buildScreens(context: Context) = launch {
        @Suppress("UNCHECKED_CAST")
        val screens = withContext(Dispatchers.Default) {
            val input = context.resources.openRawResource(R.raw.content)
            @Suppress("DEPRECATION", "BlockingMethodInNonBlockingContext")
            val rawJson = IOUtils.toString(input)
            @Suppress("BlockingMethodInNonBlockingContext")
            input.close()

            val type = object : TypeToken<ArrayList<Screen>>() {}.type
            Gson().fromJson(rawJson, type) as List<Screen>
        }

        _screens.postValue(screens)
        _firstWidget.postValue(screens.first { it.first ?: false })
    }
}