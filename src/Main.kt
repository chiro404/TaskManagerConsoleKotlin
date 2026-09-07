import enums.Priority
import enums.Status
import model.Task
import repo.FileTaskRepository
import repo.InMemoryTaskRepository
import repo.TaskRepository
import result.SearchResult
import result.TaskResult
import java.io.File
import java.io.IOException

fun main() {

    //CRUD
//    writeFile()
    menu()
    val task1 = Task(1, "task1", "chiro", Priority.MEDIUM, Status.DONE)
    val task2 = Task(2, "task2", "chiro1", Priority.HIGH, Status.IN_PROGRESS)
    val task3 = task2.copy(status = Status.DONE)
    val tasks = listOf(task1, task2, task3)

//    val (id, title, _, priority) = task2
//    println(id)
//    println(title)
//    println(priority.name)
    val isDone: (Task) -> Boolean = { it.status == Status.DONE }
    val isHighTask: (Task) -> Boolean = { it.priority == Priority.HIGH }
    val highTask = tasks.filter(isHighTask)

    val titles = tasks.map { it.title }

    val highTaskTitles = tasks.filter(isHighTask).map { it.title }

    highTaskTitles.forEach { println(it) }
    val hasHighTask = tasks.any(isHighTask)
    val allTaskHigh = tasks.all(isHighTask)
    val noHighTask = tasks.none(isHighTask)
    val firstHighTask = tasks.find(isHighTask)
    val firstHigh = tasks.firstOrNull(isHighTask)
    val lastHighTask = tasks.lastOrNull(isHighTask)

    val numbers = listOf(1, 2, 3, 4, 5)
    val sum = numbers.fold(0) { sum, element -> sum + element }

    val totalTitleLength = tasks.fold(0) { acc, element -> sum + element.title.length }
    numbers.reduce { acc, element ->
        acc + element
    }

    val totalDone = tasks.fold(0) { acc, task ->
        if (task.status == Status.DONE) acc + 1 else acc
    }

    val numbers1 = listOf(10, 20, 30, 40)
    val total = numbers.reduce { acc, element -> acc + element }
    println(numbers1)
    println(taskToString(task2))

    val taskDemo = listOf(
        Task(
            id = 1,
            title = "Learn Kotlin",
            description = "Study File I/O",
            priority = Priority.HIGH,
            status = Status.IN_PROGRESS
        ),
        Task(
            id = 2,
            title = "Learn Android",
            description = "Study Room",
            priority = Priority.MEDIUM,
            status = Status.TODO
        )
    )
    saveFile(taskDemo)
    readTaskFile().forEach { println(it) }
    loadFileTaskManager().forEach { println(it) }
}

fun loadFileTaskManager(): List<Task> {
    return readTaskFile().mapNotNull { line -> taskFromString(line) }
}

fun saveFile(tasks: List<Task>) {
    val taskSave = tasks.map { task -> taskToString(task) }
    val data = taskSave.joinToString(separator = "\n")
    writeTaskFile(data)
}

fun taskToString(task: Task): String {
    return "${task.id}|${task.title}|${task.description}|${task.priority}|${task.status}"
}


fun taskFromString(data: String): Task? {
    try {
        val parts = data.split("|")

        // Kiểm tra đủ 5 phần
        if (parts.size != 5) {
            println("Invalid task data")
            return null
        }

        // Parse ID
        val id = parts[0].toIntOrNull()
        if (id == null) {
            println("Invalid task data")
            return null
        }

        // Parse Priority
        val priority = try {
            Priority.valueOf(parts[3])
        } catch (e: IllegalArgumentException) {
            println("Invalid task data")
            return null
        }

        // Parse Status
        val status = try {
            Status.valueOf(parts[4])
        } catch (e: IllegalArgumentException) {
            println("Invalid task data")
            return null
        }

        return Task(
            id = id,
            title = parts[1],
            description = parts[2],
            priority = priority,
            status = status
        )

    } catch (e: IndexOutOfBoundsException) {
        println("Invalid task data")
        return null
    }
}

fun createDataDirectory() {
    val dir = File("data")
    if (!dir.exists()) {
        dir.mkdirs()
        println("Created data directory")
    } else {
        println("Data directory already exists")
    }
}

fun readTaskFile(): List<String> {
    val file = File("data/tasks.txt")
    if (!file.exists()) {
        println("File does not exist")
    } else {
        try {
            val contents = file.readLines()
            return contents
        } catch (e: IOException) {
            println("Error while reading data file ${e.message}")
        }
    }
    return emptyList()
}

fun appendTaskFile() {
    createDataDirectory()
    val taskFile = File("data/tasks.txt")
    try {
        taskFile.appendText(
            "\nDatabase\n" +
                    "Room\n" +
                    "Flow"
        )
        readTaskFile()
    } catch (e: IOException) {
        println("Error while writing data file ${e.message}")
    }
}

fun writeTaskFile(data: String) {
    createDataDirectory()
    val file = File("data/tasks.txt")
    try {
        file.writeText(data)
        println("Write successfully")

    } catch (e: IOException) {
        println("Write failed ${e.message}")
    }

}

fun writeFile() {
    val dataDir = File("data")
    try {
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        val file = File(dataDir, "tasks.txt")
        file.appendText("create content\n")
        file.appendText(
            "Kotlin\n" +
                    "Android\n" +
                    "Coroutine\n" +
                    "Flow"
        )
        val contentLines = file.readLines()
        for (line in contentLines) {
            println(line)
        }

    } catch (ex: IOException) {
        println("Error: ${ex.message}")
    }
}

fun filterTasks(
    repository: TaskRepository,
    condition: (Task) -> Boolean
): List<Task> {
    val tasks = repository.getAllTasks()
    return tasks.filter(condition)

}

fun menu() {
    val repository: TaskRepository = FileTaskRepository()
    while (true) {
        println("________________TASK MANAGER_________________")
        println(
            "1. Thêm task\n" +
                    "2. Xóa task\n" +
                    "3. Sửa task\n" +
                    "4. Hiển thị tất cả task\n" +
                    "5. Tìm kiếm task\n" +
                    "6. Lọc task\n" +
                    "7. Đánh dấu hoàn thành\n" +
                    "8. Thống kê\n" +
                    "9. Lưu task vào file\n" +
                    "10. Đọc task từ file\n" +
                    "11. Đồng bộ dữ liệu bất đồng bộ\n" +
                    "12. Hủy quá trình đồng bộ\n" +
                    "0. Thoát"
        )


        when (readln()) {
            "1" -> addTask(repository)
            "2" -> when (val result = deleteTask(repository)) {
                TaskResult.Success -> println("Xoa task thanh cong!")
                is TaskResult.NotFound -> println("Khong tim thay id ${result.id}!")
                is TaskResult.Error -> println(result.message)
            }

            "3" -> when (val result = editTask(repository)) {
                TaskResult.Success -> println("edit thanh cong  !")
                is TaskResult.NotFound -> println("Khong tim thay id ${result.id}!")
                is TaskResult.Error -> println(result.message)
            }

            "4" -> showTasks(repository)
            "5" -> when (val result = searchTask(repository)) {
                is SearchResult.SearchSuccess -> println(result.listSearch)
                is SearchResult.SearchNotFound -> println("Khong tim thay id ${result.keyWord}!")
                is SearchResult.SearchError -> println(result.message)
            }

            "6" -> when (val result = filterTask(repository)) {
                is SearchResult.SearchSuccess -> println(result.listSearch)
                is SearchResult.SearchNotFound -> println("Khong tim thay id ${result.keyWord}!")
                is SearchResult.SearchError -> println(result.message)
            }

            "7" -> when (val result = markComplete(repository)) {
                TaskResult.Success -> println("Task Da done !")
                is TaskResult.NotFound -> println("Khong tim thay id ${result.id}!")
                is TaskResult.Error -> println(result.message)
            }

            "8" -> showStatistics(repository)
            "0" -> return
        }
    }
}

fun showStatistics(repository: TaskRepository) {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        println("No tasks")
    } else {
        val total = tasks.count()
        val totalTodo = tasks.count { it.status == Status.TODO }
        val totalProcess = tasks.count { it.status == Status.IN_PROGRESS }
        val totalDone = tasks.count { it.status == Status.DONE }

        val totalLow = tasks.count { it.priority == Priority.LOW }
        val totalMedium = tasks.count { it.priority == Priority.MEDIUM }
        val totalHigh = tasks.count { it.priority == Priority.HIGH }


        println(
            "________ STATISTICS ________\n" +
                    "\n" +
                    "Total tasks: $total\n" +
                    "\n" +
                    "TODO: $totalTodo\n" +
                    "IN_PROGRESS: $totalProcess\n" +
                    "DONE: $totalDone\n" +
                    "\n" +
                    "LOW: $totalLow\n" +
                    "MEDIUM: $totalMedium\n" +
                    "HIGH: $totalHigh"
        )

    }

}

fun markComplete(repository: TaskRepository): TaskResult {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        return TaskResult.Error("No tasks")
    } else {
        println("Vui Long nhap ID :")
        val keySearch = readln().toIntOrNull()
        if (keySearch == null) {
            return TaskResult.Error("khong tim thay ID")
        } else {
            val task = repository.findTaskById(keySearch)
            if (task == null) {
                return TaskResult.Error("khong tim thay task")
            } else {
                val newTask = task.copy(status = Status.DONE)
                return repository.editTask(newTask)
            }
        }
    }
}

fun filterTask(repository: TaskRepository): SearchResult {
    val tasks = repository.getAllTasks()
    while (true) {
        if (tasks.isEmpty()) {
            return SearchResult.SearchError("No tasks")
        } else {
            println(
                "___Filter Task ____ " +
                        "Chọn kiểu lọc:\n" +
                        "1. Priority\n" +
                        "2. Status"
            )

            when (readln()) {
                "1" -> return filterPriority(tasks)
                "2" -> return filterStatus(tasks)
                else -> SearchResult.SearchError("Ban da nhap sai vui long nhap lai")
            }

        }

    }

}

fun filterStatus(tasks: List<Task>): SearchResult {
    while (true) {
        println(
            "___Filter Status ____" +
                    "Chọn Status:\n" +
                    "1. TODO\n" +
                    "2. IN_PROGRESS\n" +
                    "3. DONE "
        )

        val listSearch = when (readln()) {
            "1" -> tasks.filter { it.status == Status.TODO }
            "2" -> tasks.filter { it.status == Status.IN_PROGRESS }
            "3" -> tasks.filter { it.status == Status.DONE }
            else -> {
                SearchResult.SearchError("lua chon k hop ly vui long chon lai")
                continue
            }
        }
        return if (listSearch.isEmpty()) {
            SearchResult.SearchError("task not found")
        } else {
            SearchResult.SearchSuccess(listSearch)
        }
    }
}

fun filterPriority(tasks: List<Task>): SearchResult {
    while (true) {
        println(
            "___Filter priority ____" +
                    "Chọn Priority:\n" +
                    "1. LOW\n" +
                    "2. MEDIUM\n" +
                    "3. HIGH"

        )

        val listSearch = when (readln()) {
            "1" -> tasks.filter { it.priority == Priority.LOW }
            "2" -> tasks.filter { it.priority == Priority.MEDIUM }
            "3" -> tasks.filter { it.priority == Priority.HIGH }
            else -> {
                SearchResult.SearchError("lua chon k hop ly vui long chon lai")
                continue
            }
        }
        return if (listSearch.isEmpty()) {
            SearchResult.SearchError("list task not found")
        } else {
            SearchResult.SearchSuccess(listSearch)
        }
    }
}

fun searchTask(repository: TaskRepository): SearchResult {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        return SearchResult.SearchError("No tasks were found")
    } else {
        println("vui nhap task can tim kiem ")
        val keySearch = readln()
        val listSearch = tasks.filter {
            it.description.contains(keySearch, true)
                    || it.title.contains(keySearch, true)
        }

        if (listSearch.isEmpty()) {
            return SearchResult.SearchError("khong tim thay task")
        } else {
            println("DS tim thay")
            return SearchResult.SearchSuccess(listSearch)
        }
    }
}

fun editTask(repository: TaskRepository): TaskResult {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) return TaskResult.Error("No tasks were found")
    println("Nhap id can sua :  ")
    val idEdit = repository.findTaskById(readln().toIntOrNull() ?: return TaskResult.Error("No tasks were found"))
    if (idEdit == null) return TaskResult.Error("ID khong hop le ")
    println("Updated task!")
    println("vui long nhap trang thai ")
    println("vui long nhap tieu de moi  ")
    val newTitle = readln()
    println("vui long nhap noi dung moi  ")
    val newDes = readln()
    val updateID =
        idEdit.copy(
            title = newTitle,
            status = getStatus(),
            priority = getPriority(),
            description = newDes
        )
    return repository.editTask(updateID)
}

fun deleteTask(repository: TaskRepository): TaskResult {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        return TaskResult.NotFound(999999)
    } else {
        println("Nhap ID can xoa  :  ")
        val id = readln().toIntOrNull() ?: return TaskResult.Error("ID Khong Hop le!")
        return repository.deleteTask(id = id)
    }
}

fun addTask(repository: TaskRepository) {
    println("___Add task__")
    println("Vui lòng nhâp tiêu đề : ")
    val title = readln()
    println("Vui lòng nhâp nột dung: ")
    val des = readln()
    repository.addTask(
        Task(
            title = title,
            description = des,
            priority = getPriority(),
            status = getStatus()
        )
    )
}

fun getPriority(): Priority {
    while (true) {
        println(
            """
            Chọn độ ưu tiên:
            1. LOW
            2. MEDIUM
            3. HIGH
            """.trimIndent()
        )

        when (readln()) {
            "1" -> return Priority.LOW
            "2" -> return Priority.MEDIUM
            "3" -> return Priority.HIGH
            else -> println(" Lựa chọn không hợp lệ! Vui lòng nhập lại.")
        }
    }
}


fun getStatus(): Status {
    while (true) {
        println(
            """
            Chọn trạng thái:
            1. TODO
            2. IN_PROGRESS
            3. DONE
            """.trimIndent()
        )

        when (readln()) {
            "1" -> return Status.TODO
            "2" -> return Status.IN_PROGRESS
            "3" -> return Status.DONE
            else -> println("Lựa chọn không hợp lệ! Vui lòng nhập lại.")
        }
    }
}

fun showTasks(repository: TaskRepository) {
    val list = repository.getAllTasks()
    if (list.isEmpty()) {
        println("danh sach trong !!!!!!!!")
    } else {
        println("______DANH SACH TASK______")
        for (task in list) {
            println(
                """
            ID: ${task.id}
            Title: ${task.title}
            Description: ${task.description}
            Priority: ${task.priority}
            Status: ${task.status}
            --------------------------
             """.trimIndent()
            )
        }
    }
}


