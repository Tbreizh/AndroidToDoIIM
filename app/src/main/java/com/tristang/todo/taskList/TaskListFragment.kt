package com.tristang.todo.taskList
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tristang.todo.R
import com.tristang.todo.network.Api
import com.tristang.todo.network.TasksRepository
import com.tristang.todo.task.TaskActivity
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.item_task.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*


class TaskListFragment : Fragment() {

//    private val taskList = mutableListOf(
//        Task(id = "id_1", title = "Task 1", description = "description 1"),
//        Task(id = "id_2", title = "Task 2"),
//        Task(id = "id_3", title = "Task 3")
//    )

    private val coroutineScope = MainScope()

    private val tasksRepository = TasksRepository()
    private val tasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_task_list, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycle_view.adapter = TaskListAdapter(tasks)
        recycle_view.layoutManager = LinearLayoutManager(activity)
        var adapter: TaskListAdapter = recycle_view.adapter as TaskListAdapter


        addButton.setOnClickListener{
            val intent = Intent(context, TaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }


        adapter.onDeleteClickListener = {task ->
//            tasks.remove(it)
//            adapter.notifyDataSetChanged()
            coroutineScope.launch {
                tasksRepository.deleteTask(task)
            }

            adapter.notifyDataSetChanged()
        }

        adapter.onEditClickListener = {
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra("Title", it.title)
            intent.putExtra("Description", it.description)
            intent.putExtra("Id", it.id)
            startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
        }

        tasksRepository.taskList.observe(this, Observer {
            tasks.clear()
            tasks.addAll(it)
            adapter.notifyDataSetChanged()
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TASK_REQUEST_CODE){
            val task = data!!.getSerializableExtra(TaskActivity.TASK_KEY) as Task
//            this.tasks.add(task)
            coroutineScope.launch {
                tasksRepository.createTask(task)
            }
        }
        if (requestCode == EDIT_TASK_REQUEST_CODE){
            val task = data!!.getSerializableExtra(TaskActivity.TASK_KEY) as Task
//            val index = this.tasks.indexOfFirst { it.id == task.id }
//            tasks[index] = task
            coroutineScope.launch {
                tasksRepository.updateTask(task)
            }
        }

        recycle_view.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 2
        const val EDIT_TASK_REQUEST_CODE = 3
    }

    override fun onResume() {
        super.onResume()

        coroutineScope.launch {
            val userInfo = Api.userService.getInfo().body()!!
            textViewTop.text = "${userInfo.firstName} ${userInfo.lastName}"

        }

        lifecycleScope.launch {
            tasksRepository.refresh()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}
