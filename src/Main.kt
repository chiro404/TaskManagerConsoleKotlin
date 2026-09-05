import enums.Priority
import enums.Status
import model.Task
import repo.InMemoryTaskRepository
import repo.TaskRepository

fun main() {

    //CRUD
    menu()
    val task1 = Task(1, "task1", "chiro", Priority.MEDIUM, Status.DONE)
    val task2 = Task(2, "task2", "chiro1", Priority.HIGH, Status.IN_PROGRESS)
    val task3 = task2.copy(status = Status.DONE)

//    val (id, title, _, priority) = task2
//    println(id)
//    println(title)
//    println(priority.name)


}

fun menu() {
    val repository: TaskRepository = InMemoryTaskRepository()
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
            "2" -> deleteTask(repository)
            "3" -> editTask(repository)
            "4" -> showTasks(repository)
            "5" -> searchTask(repository)
            "6" -> filterTask(repository)
            "7" -> markComplete(repository)
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

fun markComplete(repository: TaskRepository) {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        println("danh sach trong !!!")
    } else {
        println("Vui Long nhap ID :")
        val keySearch = readln().toIntOrNull()
        if (keySearch == null) {
            println("Không tìm thấy task!")
        } else {
            val task = repository.findTaskById(keySearch)
            if (task == null) {
                println("Không tìm thấy task!")
            } else {
                val newTask = task.copy(status = Status.DONE)
                repository.editTask(newTask)
                println("update thanh cong !!")
            }
        }
    }
}

fun filterTask(repository: TaskRepository) {
    val tasks = repository.getAllTasks()
    while (true) {
        if (tasks.isEmpty()) {
            return println("No tasks were found")
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
                else -> println("ban nhap sai vui  long nhap lai")
            }

        }

    }

}

fun filterStatus(tasks: List<Task>) {
    while (true) {
        println(
            "___Filter Statys ____C" +
                    "họn Status:\n" +
                    "1. TODO\n" +
                    "2. IN_PROGRESS\n" +
                    "3. DONE "
        )

        val listSearch = when (readln()) {
            "1" -> tasks.filter { it.status == Status.TODO }
            "2" -> tasks.filter { it.status == Status.IN_PROGRESS }
            "3" -> tasks.filter { it.status == Status.DONE }
            else -> {
                println("lua chon k hop ly vui long chon lai")
                continue
            }
        }
        if (listSearch.isEmpty()) {
            println("k tim thay")
            return
        } else {
            for (search in listSearch) {
                println(search)
            }

        }
    }
}

fun filterPriority(tasks: List<Task>) {
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
                println("lua chon k hop ly vui long chon lai")
                continue
            }
        }
        if (listSearch.isEmpty()) {
            println("k tim thay")
            return
        } else {
            for (search in listSearch) {
                println(search)
            }

        }
    }
}

fun searchTask(repository: TaskRepository) {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        println("Danh sach trong !!!!")
    } else {
        println("vui nhap task can tim kiem ")
        val keySearch = readln()
        val listSearch = tasks.filter {
            it.description.contains(keySearch, true)
                    || it.title.contains(keySearch, true)
        }

        if (listSearch.isEmpty()) {
            println("Khong tim thay ds")
        } else {
            println("DS tim thay")
            for (taskSearch in listSearch) {
                println(taskSearch)
            }
        }
    }
}

fun editTask(repository: TaskRepository) {
    val tasks = repository.getAllTasks()
    if (repository.getAllTasks().isEmpty()) {
        println("Nothing to edit!")
    } else {
        println("Nhap id can sua :  ")
        val idEdit = tasks.find { it.id == readln().toIntOrNull() }
        if (idEdit == null) {
            println("id not found! ")
        } else {

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
            repository.editTask(updateID)
            println("update thanh cong id :${idEdit.id}")
        }
    }
}

fun deleteTask(repository: TaskRepository) {
    val tasks = repository.getAllTasks()
    if (tasks.isEmpty()) {
        println("danh sach trong!!!")
    } else {
        println("Nhap ID can xoa  :  ")
        val id = readln().toIntOrNull() ?: return
        repository.deleteTask(id = id)
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


