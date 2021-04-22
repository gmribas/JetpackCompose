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
import br.test.compose.ui.widget.Widget
import br.test.compose.ui.widget.WidgetAttributeType
import br.test.compose.ui.widget.WidgetViewClassType

/**
 *
 * Created by gmribas on 19/04/21.
 */
class ScreenFactory(private val context: Context) {

    @Composable
    fun Build(widgetJson: Widget) {
        composableInvoke(doBuild(widgetJson))
    }

    @Composable
    private fun doBuild(widgetJson: Widget): List<@Composable () -> Unit> {
        val childContent = arrayListOf<@Composable () -> Unit>()

        widgetJson.children.forEach { child ->
            when (child.viewClass) {
                WidgetViewClassType.LAYOUT, WidgetViewClassType.ROW, WidgetViewClassType.COLUMN -> {
                    childContent.addAll(doBuild(child))
                }
                else -> {
                    childContent.addAll(buildCompose(child, null))
                }
            }
        }

        return buildCompose(widgetJson, childContent)
    }

    @Composable
    private fun <T : @Composable () -> Unit> composableList(vararg items: @Composable () -> Unit): List<T> {
        @Suppress("UNCHECKED_CAST")
        return items.asList() as List<T>
    }

    @SuppressLint("ComposableNaming")
    @Composable
    private fun <T : @Composable () -> Unit> composableInvoke(items: List<T>?) {
        items?.forEach {
            @Suppress("UNCHECKED_CAST")
            (it as @Composable (() -> Unit)).invoke()
        }
    }

    @Composable
    private fun <T : @Composable () -> Unit> buildCompose(
        widget: Widget,
        content: List<T>?
    ): List<T> {
        return when (widget.viewClass) {
            WidgetViewClassType.TEXT -> {
                composableList({ BuildText(widget) })
            }
            WidgetViewClassType.INPUT -> {
                composableList({ BuildInput(widget) })
            }
            WidgetViewClassType.BUTTON -> {
                composableList({ BuildButton(widget) })
            }
            WidgetViewClassType.LAYOUT -> {
                composableList({ BuildLayout(widget, content) })
            }
            WidgetViewClassType.ROW -> {
                composableList({ BuildRow(widget, content) })
            }
            WidgetViewClassType.COLUMN -> {
                composableList({ BuildColumn(widget, content) })
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

    @Composable
    private fun BuildInput(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    @Composable
    private fun BuildButton(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    @Composable
    private fun <T : @Composable () -> Unit> BuildRow(widget: Widget, content: List<T>?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val modifier = createModifierForWidget(widget)

        Surface(
            modifier = modifier,
            elevation = 1.dp,
            color = Color(android.graphics.Color.parseColor(bgColor)),
        ) {
            Row(modifier = modifier) {
                composableInvoke(content)
            }
        }
    }

    @Composable
    private fun <T : @Composable () -> Unit> BuildColumn(widget: Widget, content: List<T>?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val modifier = createModifierForWidget(widget)

        Surface(
            modifier = modifier,
            elevation = 1.dp,
            color = Color(android.graphics.Color.parseColor(bgColor)),
        ) {
            Column(modifier = modifier) {
                composableInvoke(content)
            }
        }
    }

    @Composable
    private fun <T : @Composable () -> Unit> BuildLayout(widget: Widget, content: List<T>?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val showTopBar = widget.getAttributeByType(WidgetAttributeType.SHOW_TOP_BAR)?.value ?: ""

        val modifier = createModifierForWidget(widget)

        Scaffold(
            topBar = { if (showTopBar == "true") BuildTopBar(widget) },
            content = { composableInvoke(content) },
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
                IconButton(onClick = { }) {
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