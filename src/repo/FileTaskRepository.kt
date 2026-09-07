package repo

import model.Task
import readTaskFile
import result.TaskResult
import taskFromString
import taskToString
import writeTaskFile

class FileTaskRepository : TaskRepository {
    override fun addTask(task: Task) {
        val tasks = loadFileTaskManager().toMutableList()
        tasks.add(task)
        saveFile(tasks)
    }

    override fun deleteTask(id: Int): TaskResult {
        val tasks = loadFileTaskManager().toMutableList()

        val task = tasks.find { it.id == id }

        if (task == null) {
            return TaskResult.NotFound(id)
        }

        tasks.remove(task)

        saveFile(tasks)

        return TaskResult.Success
    }

    override fun getAllTasks(): List<Task> {
        return loadFileTaskManager()
    }

    override fun editTask(newTask: Task): TaskResult {
        val tasks = loadFileTaskManager().toMutableList()

        val index = tasks.indexOfFirst { it.id == newTask.id }

        if (index == -1) {
            return TaskResult.NotFound(newTask.id)
        }

        tasks[index] = newTask

        saveFile(tasks)

        return TaskResult.Success
    }

    override fun findTaskById(id: Int): Task? {
        return loadFileTaskManager().find { task -> task.id == id }
    }

    private fun loadFileTaskManager(): List<Task> {
        return readTaskFile()
            .mapNotNull { line ->
                taskFromString(line)
            }
    }

    private fun saveFile(tasks: List<Task>) {
        val data = tasks
            .map { task -> taskToString(task) }
            .joinToString("\n")

        writeTaskFile(data)
    }


}