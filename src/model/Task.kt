package model

import enums.Priority
import enums.Status

data class Task(
    val id: Int = id(),
    val title: String,
    val description: String,
    val priority: Priority,
    val status: Status
) {
    companion object {
        var count = 0
        private fun id() = ++count
    }
}