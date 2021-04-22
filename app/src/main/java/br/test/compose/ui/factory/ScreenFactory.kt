package br.test.compose.ui.factory

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
            childContent = when (child.viewClass) {
                WidgetViewClassType.LAYOUT, WidgetViewClassType.ROW, WidgetViewClassType.COLUMN -> {
                    doBuild(child)
                }
                else -> {
                    buildCompose(child, null)
                }
            }
        }

        return buildCompose(widgetJson, childContent)
    }

    @Composable
    private fun buildCompose(widget: Widget, content: @Composable (() -> Unit)?): @Composable () -> Unit {
        return when (widget.viewClass) {
            WidgetViewClassType.TEXT -> {
                { BuildText(widget) }
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
            WidgetViewClassType.ROW -> {
                { BuildRow(widget, content) }
            }
            WidgetViewClassType.COLUMN -> {
                { BuildColumn(widget, content) }
            }
        }
    }

    @Composable
    private fun BuildText(@Suppress("UNUSED_PARAMETER") widget: Widget) {
        val text = widget.getAttributeByType(WidgetAttributeType.TEXT)?.value ?: ""
        Text(
            text = text,
            style = MaterialTheme.typography.h6
        )
    }

    private fun buildInput(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    private fun buildButton(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    @Composable
    private fun BuildRow(@Suppress("UNUSED_PARAMETER") widget: Widget, content: @Composable (() -> Unit)?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val modifier = createModifierForWidget(widget)

        Surface(
            modifier = modifier,
            elevation = 1.dp,
            color = Color(android.graphics.Color.parseColor(bgColor)),
        ) {
            Row(modifier = modifier) {
               content?.invoke()
            }
        }
    }

    @Composable
    private fun BuildColumn(@Suppress("UNUSED_PARAMETER") widget: Widget, content: @Composable (() -> Unit)?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val modifier = createModifierForWidget(widget)

        Surface(
            modifier = modifier,
            elevation = 1.dp,
            color = Color(android.graphics.Color.parseColor(bgColor)),
        ) {
            Column(modifier = modifier) {
                content?.invoke()
            }
        }
    }

    @Composable
    private fun BuildLayout(widget: Widget, content: @Composable (() -> Unit)?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val showTopBar = widget.getAttributeByType(WidgetAttributeType.SHOW_TOP_BAR)?.value ?: ""

        val modifier = createModifierForWidget(widget)

        Scaffold(
            topBar = { if (showTopBar == "true") BuildTopBar(widget) },
            content = { content?.invoke() },
            backgroundColor = Color(android.graphics.Color.parseColor(bgColor)),
            modifier = modifier
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
            contentColor = Color.White,
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

    @SuppressLint("ModifierFactoryExtensionFunction")
    private fun createModifierForWidget(widget: Widget): Modifier {
        val w = widget.getAttributeByType(WidgetAttributeType.WIDTH)?.value ?: ""
        val h = widget.getAttributeByType(WidgetAttributeType.HEIGHT)?.value ?: ""
        val padding = widget.getAttributeByType(WidgetAttributeType.PADDING)?.value ?: ""

        var modifier = Modifier.wrapContentWidth()

        modifier = when (w) {
            "wrap_content" -> modifier.wrapContentWidth()
            "", "match_parent" -> modifier
            else -> modifier.width(Dp(w.toFloat()))
        }

        modifier = when (h) {
            "wrap_content" -> modifier.wrapContentHeight()
            "", "match_parent" -> modifier
            else -> modifier.height(Dp(h.toFloat()))
        }

        modifier = when (padding) {
            "" -> modifier
            else -> modifier.padding(Dp(padding.toFloat()))
        }
        return modifier
    }
}