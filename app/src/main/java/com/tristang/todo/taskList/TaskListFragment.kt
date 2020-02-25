package com.tristang.todo.taskList
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tristang.todo.R
import com.tristang.todo.task.TaskActivity
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.item_task.*
import java.util.*


class TaskListFragment : Fragment() {

    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

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
        recycle_view.adapter = TaskListAdapter(taskList)
        recycle_view.layoutManager = LinearLayoutManager(activity)
        var adapter: TaskListAdapter = recycle_view.adapter as TaskListAdapter


        addButton.setOnClickListener{
//            taskList.add(Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}"))
//            recycle_view.adapter!!.notifyDataSetChanged()
            val intent = Intent(context, TaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE)
        }


        adapter.onDeleteClickListener = {
            taskList.remove(it)
            adapter.notifyDataSetChanged()
        }

        adapter.onEditClickListener = {
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra("Title", it.title)
            intent.putExtra("Description", it.description)
            intent.putExtra("Id", it.id)
            startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TASK_REQUEST_CODE){
            val task = data!!.getSerializableExtra(TaskActivity.TASK_KEY) as Task
            this.taskList.add(task)
            Log.d("TEST", "yoyoy")
        }
        if (requestCode == EDIT_TASK_REQUEST_CODE){
            val task = data!!.getSerializableExtra(TaskActivity.TASK_KEY) as Task
            val index = this.taskList.indexOfFirst { it.id == task.id }
            taskList[index] = task
        }

        recycle_view.adapter?.notifyDataSetChanged()
    }

    companion object {
        const val ADD_TASK_REQUEST_CODE = 2
        const val EDIT_TASK_REQUEST_CODE = 3
    }

}
