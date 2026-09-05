package repo

import model.Task
import result.TaskResult

interface TaskRepository {

    fun addTask(task: Task)
    fun deleteTask(id : Int) : TaskResult
    fun getAllTasks(): List<Task>
    fun editTask(newTask: Task) : TaskResult
    fun findTaskById(id: Int): Task?
}