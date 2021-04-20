package br.test.compose.ui.parser

import com.google.gson.Gson

/**
 *
 * Created by gmribas on 20/04/21.
 */
object WidgetParser {

    fun parse(rawJson: String): List<Widget> {
        return Gson().fromJson<List<Widget>>(rawJson, ArrayList::class.java)
    }
}