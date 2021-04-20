package br.test.compose.ui.factory

import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
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
                buildCompose(child, null)
            }
        }

        return buildCompose(widgetJson, childContent)
    }

    @Composable
    private fun buildCompose(widget: Widget, content: @Composable (() -> Unit)?): @Composable () -> Unit {
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
                { BuildLayout(widget, content) }
            }
        }
    }

    private fun buildText(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    private fun buildInput(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    private fun buildButton(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    @Composable
    private fun BuildLayout(widget: Widget, content: @Composable (() -> Unit)?) {
        val w = widget.getAttributeByType(WidgetAttributeType.WIDTH)?.value ?: ""
        val h = widget.getAttributeByType(WidgetAttributeType.HEIGHT)?.value ?: ""
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val showTopBar = widget.getAttributeByType(WidgetAttributeType.SHOW_TOP_BAR)?.value ?: ""

        var modifiers: Modifier? = null

        if (w != "match_parent") {
            modifiers = Modifier.width(Dp(w.toFloat()))
        }
        if (h != "match_parent") {
            modifiers = Modifier.height(Dp(h.toFloat()))
        }

        Scaffold(
            topBar = { if (showTopBar == "true") BuildTopBar(widget) },
            content = { content?.invoke() },
            backgroundColor = Color(android.graphics.Color.parseColor(bgColor)),
            modifier = modifiers ?: Modifier.alpha(1f)//todo handle null modifier
        )
    }

    @Composable
    fun BuildTopBar(widget: Widget) {
        val title = widget.getAttributeByType(WidgetAttributeType.TOP_BAR_TITLE)
        val icon = widget.getAttributeByType(WidgetAttributeType.TOP_BAR_ICON)?.value ?: ""
        val color = widget.getAttributeByType(WidgetAttributeType.TOP_BAR_COLOR)?.value ?: ""
        val iconId = context.resources.getIdentifier(icon, "drawable", context.packageName)

        TopAppBar(
            title = { Text(text = title?.value ?: "") },
            backgroundColor = Color(android.graphics.Color.parseColor(color)),
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