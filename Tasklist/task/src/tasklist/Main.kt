package tasklist

import kotlin.system.exitProcess

const val FIRST_INDEX = 0

class TaskList(private val taskList:MutableList<Task> = mutableListOf()){

    fun takeInputtedTask() {
        println("Input a new task (enter a blank line to end):")
        var input = readln().trim()
        if (input == "" && taskList.isEmpty()) {
            println("The task is blank")
        } else {
            taskList.add(Task())
            taskList.last().addTask(input)
            taskList.last().task.remove(taskList.last().task.last())
        }
    }

    fun printTasks() {
        if (taskList.isEmpty()) {
            println("No tasks have been input")
        } else {
            taskList.forEach {
                print("${taskList.indexOf(it) + 1}".padEnd(3))
                it.task.forEach { it2 ->
                    if (FIRST_INDEX == it.task.indexOf(it2)) println(it2) else println(it2.padStart(it2.length + 3,' '))
                }
                println()
            }
        }
    }
}

class Task(val task: MutableList<String> = mutableListOf()) {
    fun addTask(firstInput: String) {
        task.add(firstInput)
        do {
            var nextInput = readln().trim()
            task.add(nextInput)
        } while (nextInput != "".trim())
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