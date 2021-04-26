package br.test.compose.ui.widget

import br.test.compose.ui.event.Event

/**
 *
 * Created by gmribas on 20/04/21.
 */
data class Widget(
    val id: String,
    val viewClass: WidgetViewClassType,
    val attributes: List<WidgetAttribute> = emptyList(),
    val children: List<Widget> = emptyList(),
    val event: Event?
) {

    fun getAttributeByType(type: WidgetAttributeType): WidgetAttribute? {
        return attributes.firstOrNull { it.key == type }
    }
}