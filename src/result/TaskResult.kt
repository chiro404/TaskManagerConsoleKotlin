package result


sealed class TaskResult {
    data object Success : TaskResult()
    data class Error(val message: String) : TaskResult()
    data class NotFound(val id : Int) : TaskResult()
}