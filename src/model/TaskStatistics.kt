package model

data class TaskStatistics(
    val total: Int,
    val totalTodo: Int,
    val totalProcess: Int,
    val totalDone: Int,
    val totalLow: Int,
    val totalMedium: Int,
    val totalHigh: Int
)
