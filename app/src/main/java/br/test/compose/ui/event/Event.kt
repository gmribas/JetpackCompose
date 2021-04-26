package br.test.compose.ui.event

data class Event(
    val type: EventType,
    val action: EventActionType,
    val bundle: String
)