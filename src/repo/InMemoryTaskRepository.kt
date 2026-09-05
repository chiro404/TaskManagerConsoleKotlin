package repo

import model.Task
import result.TaskResult

class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableListOf<Task>()

    override fun addTask(task: Task) {
        tasks.add(task)
    }

    override fun deleteTask(id: Int): TaskResult {
        val idDelete = tasks.find { it.id == id }
        if (idDelete != null) {
            tasks.remove(idDelete)
            return TaskResult.Success
        } else {
            return TaskResult.NotFound(id)
//          println("Khong tim thay id can xoa ")
        }
    }

    override fun getAllTasks(): List<Task> {
        return tasks.toList()
    }

    override fun editTask(newTask: Task): TaskResult {
        val index = tasks.indexOfFirst { it.id == newTask.id }
        if (index == -1) {
            return TaskResult.NotFound(newTask.id)
//            println("Khong tim thay id can sua ")
        } else {
            tasks[index] = newTask
            return TaskResult.Success
        }
    }

    override fun findTaskById(id: Int): Task? {
        return tasks.find { it.id == id }
    }
}