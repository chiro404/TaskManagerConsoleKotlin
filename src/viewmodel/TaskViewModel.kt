package viewmodel

import enums.Priority
import enums.Status
import kotlinx.coroutines.flow.StateFlow
import model.Task
import model.TaskStatistics
import repo.TaskRepository
import result.SearchResult
import result.TaskResult

class TaskViewModel(private val repository: TaskRepository) {
    val tasks: StateFlow<List<Task>> = repository.tasks

    fun addTask(task: Task) {
        repository.addTask(task)
    }

    fun deleteTask(id: Int): TaskResult {
        return repository.deleteTask(id)
    }

    fun editTask(task: Task): TaskResult {
        return repository.editTask(task)
    }

    fun findTaskById(id: Int): Task? {
        return repository.findTaskById(id)
    }

    fun searchTasks(keyWord: String): SearchResult {
        val tasks = tasks.value
        if (tasks.isEmpty()) return SearchResult.SearchError("No tasks were found")
        val listSearch = tasks.filter { it.description.contains(keyWord, true) || it.title.contains(keyWord, true) }
        return if (listSearch.isEmpty()) {
            SearchResult.SearchError(keyWord)
        } else {
            SearchResult.SearchSuccess(listSearch)
        }
    }

    fun filterByStatus(status: Status): SearchResult {
        val currentTasks = tasks.value

        if (currentTasks.isEmpty()) {
            return SearchResult.SearchError("No tasks")
        }

        val result = currentTasks.filter {
            it.status == status
        }

        return if (result.isEmpty()) {
            SearchResult.SearchNotFound(status.name)
        } else {
            SearchResult.SearchSuccess(result)
        }
    }


    fun filterByPriority(priority: Priority): SearchResult {
        val currentTasks = tasks.value

        if (currentTasks.isEmpty()) {
            return SearchResult.SearchError("No tasks")
        }

        val result = currentTasks.filter {
            it.priority == priority
        }

        return if (result.isEmpty()) {
            SearchResult.SearchNotFound(priority.name)
        } else {
            SearchResult.SearchSuccess(result)
        }
    }

    fun getStatistics(): TaskStatistics {
        val currentTasks = tasks.value

        return TaskStatistics(
            total = currentTasks.size,

            totalTodo = currentTasks.count {
                it.status == Status.TODO
            },

            totalProcess = currentTasks.count {
                it.status == Status.IN_PROGRESS
            },

            totalDone = currentTasks.count {
                it.status == Status.DONE
            },

            totalLow = currentTasks.count {
                it.priority == Priority.LOW
            },

            totalMedium = currentTasks.count {
                it.priority == Priority.MEDIUM
            },

            totalHigh = currentTasks.count {
                it.priority == Priority.HIGH
            }
        )
    }


    fun markComplete(id: Int): TaskResult {
        val task = tasks.value.find { it.id == id }

        if (task == null) {
            return TaskResult.NotFound(id)
        }

        val completedTask = task.copy(
            status = Status.DONE
        )

        return repository.editTask(completedTask)
    }
}