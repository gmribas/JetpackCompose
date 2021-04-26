package br.test.compose.ui.screen

import br.test.compose.ui.widget.Widget

data class Screen(
    val id: String,
    val first: Boolean?,
    val widget: Widget)