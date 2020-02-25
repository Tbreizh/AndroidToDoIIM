package com.tristang.todo.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tristang.todo.taskList.Task

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    // Ces deux variables encapsulent la même donnée:
    // [_taskList] est modifiable et privée:
    // On va l'utiliser seulement dans le contexte de cette classe
    private val _taskList = MutableLiveData<List<Task>>()
    // [taskList] est publique mais non-modifiable:
    // On pourra seulement l'observer (s'y abonner) depuis d'autres classes
    public val taskList: LiveData<List<Task>> = _taskList

    suspend fun updateTask(task: Task) {
        tasksWebService.updateTask(task)
        val editableList = _taskList.value.orEmpty().toMutableList()
        val position = editableList.indexOfFirst { task.id == it.id }
        editableList[position] = task
        _taskList.value = editableList
    }

    suspend fun createTask(task: Task) {
        val mutedTask = tasksWebService.createTask(task)?.body()!!
        val editableList = _taskList.value.orEmpty().toMutableList()
        editableList.add(mutedTask)
        _taskList.value = editableList
    }

    suspend fun deleteTask(task: Task) {
        tasksWebService.deleteTask(task.id)
        val editableList = _taskList.value.orEmpty().toMutableList()
        editableList.remove(task)
        _taskList.value = editableList
    }

    suspend fun refresh() {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.getTasks()
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val fetchedTasks = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            _taskList.value = fetchedTasks
        }
    }

}