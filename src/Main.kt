import enums.Priority
import enums.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import model.Task
import repo.FileTaskRepository
import repo.TaskRepository
import result.SearchResult
import result.TaskResult
import kotlinx.coroutines.flow.map
import viewmodel.TaskViewModel
import java.io.File
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

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

    runBlocking {
        println("before colect")
        numberFlow().collect { println(it) }
        println("after colect")
        numberFlow1().collect { println("Received:$it") }
        stringsFlow.flowOn(Dispatchers.IO).map { it.uppercase() }.collect {
            println("Collect thread: ${Thread.currentThread().name}")
            println(it)
        }
        numFlows.buffer().collect {
            println(it)
            delay(1000)
        }
//         buffer cho phep emit tiep tuc chay trong khi collect xu ly data
//         collect


        launch {
            println("Collect lần 1")
            numberFlow1().collect {
                println(it)
            }
        }
        taskFlow().collect { println(it) }

        launch {
            println("Collect lần 2")

            numberFlow().collect {
                println(it)
            }
        }
    }

}

val numFlows = flow {
    for (i in 1..5) {
        println("emit: $i")
        emit(i)
    }
}

val stringsFlow = flow {
    println("Flow thread: ${Thread.currentThread().name}")
    emit("one")
    emit("two")
    emit("three")
    emit("four")
}

fun taskFlow() = flow {
    emit("Task 1")
    delay(1000)
    emit("Task 2")
    delay(1000)
    emit("Task 3")
}


// flow
fun numberFlow() = flow {
    println("Flow started")
    delay(1000)
    emit(1)
    delay(1000)
    emit(2)
    delay(1000)
    emit(3)
    delay(1000)
    emit(4)
    delay(1000)
    emit(5)
    delay(1000)
    emit(6)
    delay(1000)
    emit(7)
}

// flow
fun numberFlow1() = flow {
    println("Flow started")
    delay(1000)
    emit(10)
    delay(1000)
    emit(20)
    delay(1000)
    emit(30)
    delay(1000)
    emit(40)
    delay(1000)
    emit(50)
    delay(1000)
    emit(60)
    delay(1000)
    emit(70)
}

suspend fun syncTask(viewModel: TaskViewModel) {
    try {
        println("Starting sync task")
        delay(1000)
        println("loading task ...")
        currentCoroutineContext().ensureActive()
        val tasks = withContext(Dispatchers.IO) {
            viewModel.tasks.value
        }
        currentCoroutineContext().ensureActive()
        delay(1000)
        println("Found ${tasks.size} tasks")
        delay(1000)
        println("Sync complete")
    } catch (e: CancellationException) {
        println("sync  Cancelled ")
    }
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
    val tasks = repository.tasks.value
    return tasks.filter(condition)

}

fun menu() {
    val repository: TaskRepository = FileTaskRepository()
    val viewModel = TaskViewModel(repository)
    val scope = CoroutineScope(Dispatchers.Default)
    var job: Job? = null
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
            "1" -> addTask(viewModel)
            "2" -> when (val result = deleteTask(viewModel)) {
                TaskResult.Success -> println("Xoa task thanh cong!")
                is TaskResult.NotFound -> println("Khong tim thay id ${result.id}!")
                is TaskResult.Error -> println(result.message)
            }

            "3" -> when (val result = editTask(viewModel)) {
                TaskResult.Success -> println("edit thanh cong  !")
                is TaskResult.NotFound -> println("Khong tim thay id ${result.id}!")
                is TaskResult.Error -> println(result.message)
            }

            "4" -> showTasks(viewModel)
            "5" -> when (val result = searchTask(viewModel)) {
                is SearchResult.SearchSuccess -> println(result.listSearch)
                is SearchResult.SearchNotFound -> println("Khong tim thay id ${result.keyWord}!")
                is SearchResult.SearchError -> println(result.message)
            }

            "6" -> when (val result = filterTask(viewModel)) {
                is SearchResult.SearchSuccess -> println(result.listSearch)
                is SearchResult.SearchNotFound -> println("Khong tim thay id ${result.keyWord}!")
                is SearchResult.SearchError -> println(result.message)
            }

            "7" -> when (val result = markComplete(viewModel)) {
                TaskResult.Success -> println("Task Da done !")
                is TaskResult.NotFound -> println("Khong tim thay id ${result.id}!")
                is TaskResult.Error -> println(result.message)
            }

            "8" -> showStatistics(viewModel)
            "11" -> {
                if (job?.isActive == true) {
                    println("Sync is already running")
                } else {
                    job = scope.launch {
                        syncTask(viewModel)
                    }
                }
            }

            "12" -> job?.cancel()

            "0" -> return
        }
    }


}

fun showStatistics(viewModel: TaskViewModel) {
    val statistics = viewModel.getStatistics()

    if (statistics.total == 0) {
        println("No tasks")
        return
    }

    println(
        """
        ________ STATISTICS ________

        Total tasks: ${statistics.total}

        TODO: ${statistics.totalTodo}
        IN_PROGRESS: ${statistics.totalProcess}
        DONE: ${statistics.totalDone}

        LOW: ${statistics.totalLow}
        MEDIUM: ${statistics.totalMedium}
        HIGH: ${statistics.totalHigh}
        """.trimIndent()
    )
}

fun markComplete(viewModel: TaskViewModel): TaskResult {
    println("Vui Long nhap ID :")
    val keySearch = readln().toIntOrNull() ?: return TaskResult.Error("khong tim thay ID")
    return viewModel.markComplete(keySearch)
}


fun filterTask(viewModel: TaskViewModel): SearchResult {
    while (true) {
        println(
            "___Filter Task ____ " +
                    "Chọn kiểu lọc:\n" +
                    "1. Priority\n" +
                    "2. Status"
        )

        when (readln()) {
            "1" -> return filterPriority(viewModel)
            "2" -> return filterStatus(viewModel)
            else -> SearchResult.SearchError("Ban da nhap sai vui long nhap lai")


        }

    }

}

fun filterStatus(viewModel: TaskViewModel): SearchResult {
    while (true) {
        println(
            "___Filter Status ____" +
                    "Chọn Status:\n" +
                    "1. TODO\n" +
                    "2. IN_PROGRESS\n" +
                    "3. DONE "
        )

        when (readln()) {
            "1" -> viewModel.filterByStatus(Status.TODO)
            "2" -> viewModel.filterByStatus(Status.IN_PROGRESS)
            "3" -> viewModel.filterByStatus(Status.DONE)
            else -> {
                SearchResult.SearchError("lua chon k hop ly vui long chon lai")
            }
        }
    }
}

fun filterPriority(viewModel: TaskViewModel): SearchResult {
    while (true) {
        println(
            "___Filter priority ____" +
                    "Chọn Priority:\n" +
                    "1. LOW\n" +
                    "2. MEDIUM\n" +
                    "3. HIGH"

        )
        when (readln()) {
            "1" -> viewModel.filterByPriority(Priority.LOW)
            "2" -> viewModel.filterByPriority(Priority.MEDIUM)
            "3" -> viewModel.filterByPriority(Priority.HIGH)
            else -> {
                SearchResult.SearchError("lua chon k hop ly vui long chon lai")
            }
        }
    }
}

fun searchTask(viewModel: TaskViewModel): SearchResult {
    println("vui nhap task can tim kiem ")
    val keySearch = readln()
    return viewModel.searchTasks(keySearch)
}

fun editTask(viewModel: TaskViewModel): TaskResult {
    val tasks = viewModel.tasks.value
    if (tasks.isEmpty()) return TaskResult.Error("No tasks were found")
    println("Nhap id can sua :  ")
    val idEdit = viewModel.findTaskById(readln().toIntOrNull() ?: return TaskResult.Error("No tasks were found"))
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
    return viewModel.editTask(updateID)
}

fun deleteTask(viewModel: TaskViewModel): TaskResult {
    val tasks = viewModel.tasks.value
    if (tasks.isEmpty()) {
        return TaskResult.NotFound(999999)
    } else {
        println("Nhap ID can xoa  :  ")
        val id = readln().toIntOrNull() ?: return TaskResult.Error("ID Khong Hop le!")
        return viewModel.deleteTask(id)
    }
}

fun addTask(viewModel: TaskViewModel) {
    println("___Add task__")
    println("Vui lòng nhâp tiêu đề : ")
    val title = readln()
    println("Vui lòng nhâp nột dung: ")
    val des = readln()
    viewModel.addTask(
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

fun showTasks(viewModel: TaskViewModel) {
    val list = viewModel.tasks.value
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


