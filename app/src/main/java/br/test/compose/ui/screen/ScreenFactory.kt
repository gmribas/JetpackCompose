package br.test.compose.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.test.compose.ui.NavigationViewModel
import br.test.compose.ui.event.EventFactory
import br.test.compose.ui.widget.Widget
import br.test.compose.ui.widget.WidgetAttributeType
import br.test.compose.ui.widget.WidgetViewClassType

/**
 *
 * Created by gmribas on 19/04/21.
 */
class ScreenFactory(private val context: Context, private val navigationViewModel: NavigationViewModel) {

    private val eventFactory by lazy {
        EventFactory(context, navigationViewModel) { id, visible ->
            changeVisibility(id, visible)
        }
    }
    private lateinit var visibleState: HashMap<String, MutableState<Boolean>>

    @Composable
    fun Build(widgetJson: Widget) {
        visibleState = rememberSaveable { hashMapOf() }
        composableInvoke(doBuild(widgetJson))
    }

    @Composable
    private fun doBuild(widgetJson: Widget): List<@Composable () -> Unit> {
        if (widgetJson.viewClass == WidgetViewClassType.EMPTY_VIEW) {
            return emptyList()
        }

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
    private fun composableInvoke(items: List<@Composable () -> Unit>?) {
        items?.forEach {
            it.invoke()
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
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
            WidgetViewClassType.CIRCULAR_PROGRESS -> {
                composableList({ BuildCircularProgress(widget) })
            }
            WidgetViewClassType.EMPTY_VIEW -> throw IllegalStateException()
        }
    }

    @Composable
    private fun BuildText(widget: Widget) {
        val text = widget.getAttributeByType(WidgetAttributeType.TEXT)?.value ?: ""
        Text(
            text = text,
            style = MaterialTheme.typography.h6
        )
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun BuildCircularProgress(widget: Widget) {
        val modifier = createModifierForWidget(widget)
        val visible = initVisibility(widget)

        AnimatedVisibility(visible = visible.value) {
            CircularProgressIndicator(modifier = modifier)
        }
    }

    @Composable
    private fun BuildInput(@Suppress("UNUSED_PARAMETER") widget: Widget) {

    }

    @Composable
    private fun BuildButton(widget: Widget) {
        val text = widget.getAttributeByType(WidgetAttributeType.TEXT)?.value ?: ""
        val modifier = createModifierForWidget(widget)
        val trigger = remember { mutableStateOf(false) }
        var eventTrigger: @Composable (() -> Unit)? = null

        OutlinedButton(
            modifier = modifier,
            onClick = { trigger.value = true }
        ) {
            Text(text = text)
        }

        if (eventTrigger == null) {
            widget.event?.let {
                eventTrigger = eventFactory.build(it)
            }
        }

        if (trigger.value) {
            eventTrigger?.invoke()
            trigger.value = false
        }
    }

    @Composable
    private fun BuildRow(widget: Widget, content: List<@Composable () -> Unit>?) {
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
    private fun BuildColumn(widget: Widget, content: List<@Composable () -> Unit>?) {
        val bgColor = widget.getAttributeByType(WidgetAttributeType.BACKGROUND_COLOR)?.value ?: ""
        val modifier = createModifierForWidget(widget)

        Surface(
            modifier = modifier,
            elevation = 1.dp,
            color = Color(android.graphics.Color.parseColor(bgColor))
        ) {
            Column(modifier = modifier) {
                composableInvoke(content)
            }
        }
    }

    @Composable
    private fun BuildLayout(widget: Widget, content: List<@Composable () -> Unit>?) {
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

        var modifier = Modifier.alpha(1f)

        modifier = when (w) {
            "" -> modifier
            "wrap_content" -> modifier.wrapContentWidth()
            "match_parent" -> modifier.fillMaxWidth()
            else -> modifier.width(Dp(w.toFloat()))
        }

        modifier = when (h) {
            "" -> modifier
            "wrap_content" -> modifier.wrapContentHeight()
            "match_parent" -> modifier.fillMaxHeight()
            else -> modifier.height(Dp(h.toFloat()))
        }

        modifier = when (padding) {
            "" -> modifier
            else -> modifier.padding(Dp(padding.toFloat()))
        }

        return modifier
    }

    @Composable
    private fun initVisibility(widget: Widget): MutableState<Boolean> {
        val visible = visibleState[widget.id] ?: remember { mutableStateOf(true) }
        visibleState[widget.id] = visible
        return visible
    }

    private fun changeVisibility(id: String, visible: Boolean) {
        visibleState[id]?.let {
            it.value = visible
        }
    }
}