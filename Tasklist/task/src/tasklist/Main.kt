package tasklist

import kotlin.system.exitProcess

const val FIRST_INDEX = 0

class TaskList {
    private val tasks = mutableListOf<MutableList<String>>()

    fun takeInputtedTask() {
        println("Input a new task (enter a blank line to end):")
        var input = readln().trim()
        if (input == "" && tasks.isEmpty()) {
            println("The task is blank")
        } else {
            tasks.add(mutableListOf())
            tasks.last().add(input)
            do {
                input = readln().trim()
                tasks.last().add(input)
            } while (input != "".trim())
            tasks.last().remove(tasks.last().last())
        }
    }

    fun printTasks() {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
        } else {
            /*for (i in tasks.indices) {
                println("${i + 1} ".padEnd(3, ' ') + tasks[i])*/
            tasks.forEach {
                print("${tasks.indexOf(it) + 1}".padEnd(3))
                it.forEach { it2 ->
                    if (FIRST_INDEX == it.indexOf(it2)) println(it2) else println(it2.padStart(it2.length + 3,' '))
                }
                println()
            }
        }
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


