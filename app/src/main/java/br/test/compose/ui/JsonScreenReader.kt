package br.test.compose.ui

import br.test.compose.R
import br.test.compose.ui.screen.Screen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.flow

object JsonScreenReader {

    fun read() = flow {
        val rawJson = ResourceProvider
            .resource
            .openRawResource(R.raw.content)
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<ArrayList<Screen>>() {}.type
        val screens = Gson().fromJson(rawJson, type) as List<Screen>
        emit(screens)
    }
}