package repo

import model.Task

class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableListOf<Task>()

    override fun addTask(task: Task) {
        tasks.add(task)
    }

    override fun deleteTask(id: Int) {
        val idDelete = tasks.find { it.id == id }
        if (idDelete != null) {
            tasks.remove(idDelete)
        } else {
            println("Khong tim thay id can xoa ")
        }
    }

    override fun getAllTasks(): List<Task> {
        return tasks.toList()
    }

    override fun editTask(newTask: Task) {
        val index = tasks.indexOfFirst { it.id == newTask.id }
        if (index == -1) {
            println("Khong tim thay id can sua ")
        } else {
            tasks[index] = newTask
        }

    }

    override fun findTaskById(id: Int): Task? {
        return tasks.find { it.id == id }
    }
}