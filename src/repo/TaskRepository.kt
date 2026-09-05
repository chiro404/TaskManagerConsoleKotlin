package repo

import model.Task

interface TaskRepository {

    fun addTask(task: Task)
    fun deleteTask(id : Int)
    fun getAllTasks(): List<Task>
    fun editTask(newTask: Task)
    fun findTaskById(id: Int): Task?
}