package tasklist

import kotlin.system.exitProcess
import kotlinx.datetime.*
import java.io.File
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

const val NUMBER_OF_FIRST_ELEMENT = 1
const val NUMBERS_PADDING = 3
const val SIZE_OF_TASKS_FIELD = 44
const val SIZE_OF_HOUR_FORMAT = 2
const val SIZE_OF_MINUTE_FORMAT = 2
const val SIZE_OF_YEAR_FORMAT = 4
const val SIZE_OF_MONTH_FORMAT = 2
const val SIZE_OF_DAY_FORMAT = 2

enum class Priority(val color: String) {
    CRITICAL("\u001B[101m \u001B[0m"),
    HIGH("\u001B[103m \u001B[0m"),
    NORMAL("\u001B[102m \u001B[0m"),
    LOW("\u001B[104m \u001B[0m")
}

enum class TaskStatus(val color: String) {
    IN_TIME("\u001B[102m \u001B[0m"),
    TODAY("\u001B[103m \u001B[0m"),
    OVERDUE( "\u001B[101m \u001B[0m")
}

class TaskList(val taskList:MutableList<Task> = mutableListOf()) {

    fun takeInputtedTask() {
        taskList.add(Task())
        taskList.last().addPriority()
        taskList.last().addDeadlinesDate()
        taskList.last().addDeadlinesTime()
        println("Input a new task (enter a blank line to end):")
        val input = readln().trim()
        if (input == "") {
            println("The task is blank")
            taskList.remove(taskList.last())
        } else {
            taskList.last().addTask(input)
            taskList.last().task.remove(taskList.last().task.last())
        }
    }

    fun printTasks() {
        if (taskList.isEmpty()) {
            println("No tasks have been input")
        } else {
            printHeaders()
            taskList.forEach {
                val number = taskList.indexOf(it) + 1
                val paddedNumber = "| ${number.toString().padEnd(NUMBERS_PADDING)}"
                val date = "| ${it.date.paddedYear}-${it.date.paddedMonth}-${it.date.paddedDay} "
                val time = "| ${it.time.paddedHours}:${it.time.paddedMinutes} "
                val priorityTag = "| ${it.priority.color} "
                val taskStatus = "| ${it.getTaskStatusColor()} "
                val firstTaskList = it.task.first().chunked(SIZE_OF_TASKS_FIELD)
                val firstTask = firstFormattedTask(firstTaskList)
                print("$paddedNumber$date$time$priorityTag$taskStatus$firstTask")
                it.task.forEach loop@{ it2 ->
                    if (it.task.indexOf(it2) == 0) {
                        return@loop
                    } else {
                        val nTaskList = it2.chunked(SIZE_OF_TASKS_FIELD)
                        print(nextFormattedTask(nTaskList))
                    }
                }
                println("+----+------------+-------+---+---+--------------------------------------------+")
            }
        }
    }

    private fun firstFormattedTask(list: List<String>): String {
        if (list.size == 1)  {
            return "|${list.first().padEnd(SIZE_OF_TASKS_FIELD, ' ')}|\n"
        } else {
            val firstLine = "|${list.first().padEnd(SIZE_OF_TASKS_FIELD, ' ')}|\n"
            val nextLines = StringBuilder()
            list.forEach loop@{
                if (list.indexOf(it) == 0) return@loop
                nextLines.append("|    |            |       |   |   |${it.padEnd(SIZE_OF_TASKS_FIELD, ' ')}|\n")
            }
            return "$firstLine$nextLines"
        }
    }

    private fun nextFormattedTask(list: List<String>): String {
        val nextLines = StringBuilder()
        list.forEach {
            nextLines.append("|    |            |       |   |   |${it.padEnd(SIZE_OF_TASKS_FIELD, ' ')}|\n")
        }
        return "$nextLines"
    }
    private fun printHeaders() {
        println("""
            +----+------------+-------+---+---+--------------------------------------------+
            | N  |    Date    | Time  | P | D |                   Task                     |
            +----+------------+-------+---+---+--------------------------------------------+
        """.trimIndent())
    }

    fun deleteTask() {
        printTasks()
        if (taskList.isEmpty()) return
        val currentTaskListSize = taskList.size
        do {
            println("Input the task number (1-${taskList.size}):")
            val input = try {
                readln().toInt()
            } catch (e: Exception) {
                0
            }
            try {
                taskList.removeAt(input - 1)
                println("The task is deleted")
            } catch (e: Exception) {
                println("Invalid task number")
            }
        } while (input !in NUMBER_OF_FIRST_ELEMENT..currentTaskListSize)
    }

    fun editTasks() {
        printTasks()
        if (taskList.isEmpty()) return
        var result: Boolean
        do {
            println("Input the task number (1-${taskList.size}):")
            val input = try { readln().toInt() } catch (e: Exception) { 0 }
            result = try {
                taskList[input - 1].editTaskProperty()
                true
            } catch (e: Exception) {
                println("Invalid task number")
                false
            }
        } while (!result)
    }
}

class Task(val task: MutableList<String> = mutableListOf()) {
    var priority = Priority.NORMAL
    lateinit var date:Date
    lateinit var time:Time

    fun addTask(firstInput: String) {
        task.add(firstInput)
        do {
            val nextInput = readln().trim()
            task.add(nextInput)
        } while (nextInput != "".trim())
    }

    fun addPriority() {
        var result: Priority?
        do {
            println("Input the task priority (C, H, N, L):")
            val input = readln().lowercase()
            result = when (input) {
                "c" -> Priority.CRITICAL
                "h" -> Priority.HIGH
                "n" -> Priority.NORMAL
                "l" -> Priority.LOW
                else -> null
            }
        } while (result == null)
        priority = result
    }

    fun addDeadlinesDate() {
        var legitDate: Boolean
        do {
            println("Input the date (yyyy-mm-dd):")
            try {
                legitDate = true
                val (year, month, day) = readln().split("-").map { it.toInt() }
                LocalDate(year, month, day)
                date = Date(year,month,day)
            } catch (e: Exception) {
                legitDate = false
                println("The input date is invalid")
            }
        } while (!legitDate)
    }

    fun addDeadlinesTime() {
        var legitTime: Boolean
        do {
            println("Input the time (hh:mm):")
            try {
                legitTime = true
                val (hours, minutes) = readln().split(":").map { it.padStart(SIZE_OF_HOUR_FORMAT, '0').toInt() }
                LocalDateTime(date.year, date.month, date.day, hours, minutes)
                time = Time(hours, minutes)
            } catch (e: Exception) {
                legitTime = false
                println("The input time is invalid")
            }
        } while (!legitTime)
    }

    fun editTaskProperty() {
        println("Input a field to edit (priority, date, time, task):")
        when (readln().lowercase()) {
            "priority" -> {
                addPriority()
                println("The task is changed")
            }
            "date" -> {
                addDeadlinesDate()
                println("The task is changed")
            }
            "time" -> {
                addDeadlinesTime()
                println("The task is changed")
            }
            "task" -> {
                editTask()
                println("The task is changed")
            }
            else -> {
                println("Invalid field")
                editTaskProperty()
            }
        }
    }

    private fun editTask() {
        task.clear()
        println("Input a new task (enter a blank line to end):")
        val firstInput = readln().trim()
        if (firstInput == "") {
            println("The task is blank")
            editTask()
            return
        }
        task.add(firstInput)
        do {
            val input = readln().trim()
            task.add(input)
        } while (input != "".trim())
    }

    fun getTaskStatusColor(): String {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+2")).date
        val numberOfDays = currentDate.daysUntil(LocalDate(date.year, date.month, date.day))
        return if (numberOfDays > 0) {
            TaskStatus.IN_TIME.color
        } else if (numberOfDays == 0) {
            TaskStatus.TODAY.color
        } else {
            TaskStatus.OVERDUE.color
        }
    }
}

data class Date(val year:Int, val month:Int, val day:Int) {
    val paddedYear = this.year.toString().padStart(SIZE_OF_YEAR_FORMAT, '0')
    val paddedMonth = this.month.toString().padStart(SIZE_OF_MONTH_FORMAT, '0')
    val paddedDay = this.day.toString().padStart(SIZE_OF_DAY_FORMAT, '0')
}

data class Time(val hours:Int, val minutes: Int) {
    val paddedHours = this.hours.toString().padStart(SIZE_OF_HOUR_FORMAT,  '0')
    val paddedMinutes = this.minutes.toString().padStart(SIZE_OF_MINUTE_FORMAT, '0')
}
fun main() {
    val taskList = TaskList()
    do {
        println("Input an action (add, print, edit, delete, end):")
        val inputtedAction = readln().lowercase().trim()
        actionProcessor(inputtedAction, taskList)
        saveToJSON(taskList)
    } while (inputtedAction != "end")
}

fun actionProcessor(action: String, taskList: TaskList) {
    when (action) {
        "add" -> taskList.takeInputtedTask()
        "print" -> taskList.printTasks()
        "edit" -> taskList.editTasks()
        "delete" -> taskList.deleteTask()
        "end" -> exit()
        else -> println("The input action is invalid")
    }
}

fun saveToJSON(tasklist: TaskList) {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
    val tasklistAdapter = moshi.adapter<MutableList<Task>>(type)
    val tasklistAsJSON = tasklistAdapter.toJson(tasklist.taskList)
    val file = File("tasklist.json")
    file.writeText(tasklistAsJSON)
}

fun exit() {
    println("Tasklist exiting!")
    exitProcess(0)
}
