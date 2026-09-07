package repo

import kotlinx.coroutines.flow.StateFlow
import model.Task
import result.TaskResult

interface TaskRepository {

    val tasks: StateFlow<List<Task>>
    fun addTask(task: Task)
    fun deleteTask(id : Int) : TaskResult
    fun editTask(newTask: Task) : TaskResult
    fun findTaskById(id: Int): Task?
}