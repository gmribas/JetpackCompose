package br.test.compose.ui.factory

import android.content.Context
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.test.compose.ui.parser.Widget
import br.test.compose.ui.parser.WidgetAttributeType
import br.test.compose.ui.parser.WidgetViewClassType

/**
 *
 * Created by gmribas on 19/04/21.
 */
class ScreenFactory(private val context: Context) {

    @Composable
    fun Build(widgetJson: Widget) {
        doBuild(widgetJson).invoke()
    }

    @Composable
    private fun doBuild(widgetJson: Widget): @Composable () -> Unit {
        var childContent: @Composable (() -> Unit)? = null
        widgetJson.children.forEach { child ->
            childContent = if (child.viewClass == WidgetViewClassType.LAYOUT) {
                doBuild(child)
            } else {
                BuildCompose(child, null)
            }
        }

        requireNotNull(childContent)
        val result = BuildCompose(widgetJson, childContent)
        requireNotNull(result)
        return result
    }

    @Composable
    private fun BuildCompose(widget: Widget, content: @Composable (() -> Unit)?): @Composable () -> Unit {
        return when (widget.viewClass) {
            WidgetViewClassType.TEXT -> {
                { buildText(widget) }
            }
            WidgetViewClassType.INPUT -> {
                { buildInput(widget) }
            }
            WidgetViewClassType.BUTTON -> {
                { buildButton(widget) }
            }
            WidgetViewClassType.LAYOUT -> {
                requireNotNull(content)
                { BuildLayout(widget, content!!) }
            }
        }
    }

    private fun buildText(widget: Widget) {

    }

    private fun buildInput(widget: Widget) {

    }

    private fun buildButton(widget: Widget) {

    }

    @Composable
    private fun BuildLayout(widget: Widget, content: @Composable () -> Unit) {
        Scaffold(
            topBar = { buildTopBar(widget) },
            content = { content.invoke() }
        )
    }

    @Composable
    fun buildTopBar(widget: Widget) {
        val title = widget.getAttributeByType(WidgetAttributeType.TOP_BAR_TITLE)
        val icon = widget.getAttributeByType(WidgetAttributeType.TOP_BAR_ICON)?.value ?: ""
        val iconId = context.resources.getIdentifier(icon, "drawable", context.packageName)

        TopAppBar(
            title = { Text(text = title?.value ?: "") },
            navigationIcon = {
                IconButton(onClick = {  }) {
                    Icon(
                        painter = painterResource(iconId),
                        contentDescription = ""
                    )
                }
            }
        )
    }
}