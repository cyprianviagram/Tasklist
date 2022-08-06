package tasklist

import kotlin.system.exitProcess
import kotlinx.datetime.*

const val FIRST_INDEX = 0

enum class Priority(val tag: String) {
    CRITICAL("C"),
    HIGH("H"),
    NORMAL("N"),
    LOW("L")
}

class TaskList(private val taskList:MutableList<Task> = mutableListOf()){

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
            taskList.forEach {
                val number = "${taskList.indexOf(it) + 1}".padEnd(3)
                val date = "${it.date.year.toString().padStart(4, '0')}-${it.date.monthNumber.toString().padStart(2, '0')}-${it.date.dayOfMonth.toString().padStart(2, '0')}"
                val time = "${it.date.hour.toString().padStart(2, '0')}:${it.date.minute.toString().padStart(2, '0')}"
                println("$number$date $time ${it.priority.tag}")
                it.task.forEach { it2 ->
                    println(it2.padStart(it2.length + 3,' '))
                }
                println()
            }
        }
    }
}

class Task(val task: MutableList<String> = mutableListOf()) {
    var priority = Priority.NORMAL
    var date = Clock.System.now().toLocalDateTime((TimeZone.of("UTC+0")))
    fun addTask(firstInput: String) {
        task.add(firstInput)
        do {
            val nextInput = readln().trim()
            task.add(nextInput)
        } while (nextInput != "".trim())
    }

    fun addPriority() {
        loop@do {
            println("Input the task priority (C, H, N, L):")
            val input = readln().lowercase()
            priority = when (input) {
                "c" -> Priority.CRITICAL
                "h" -> Priority.HIGH
                "n" -> return
                "l" -> Priority.LOW
                else -> continue@loop
            }
        } while (input !in "chnl")
    }

    fun addDeadlinesDate() {
        var legitDate: Boolean
        do {
            println("Input the date (yyyy-mm-dd):")
            try {
                legitDate = true
                val (year, month, day) = readln().split("-").map { it.toInt() }
                date = LocalDateTime(year, month, day, date.hour, date.minute)
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
                val (hours, minutes) = readln().split(":").map { it.padStart(2, '0').toInt() }
                date = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hours, minutes)
            } catch (e: Exception) {
                legitTime = false
                println("The input time is invalid")
            }
        } while (!legitTime)
    }
}


fun main() {
    val taskList = TaskList()
    do {
        println("Input an action (add, print, end):")
        val inputtedAction = readln().lowercase().trim()
        actionProcessor(inputtedAction, taskList)
    } while (inputtedAction != "end")
}

fun actionProcessor(action: String, taskList: TaskList) {
    when (action) {
        "add" -> taskList.takeInputtedTask()
        "print" -> taskList.printTasks()
        "end" -> exit()
        else -> println("The input action is invalid")
    }
}

fun exit() {
    println("Tasklist exiting!")
    exitProcess(0)
}