package com.tristang.todo.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tristang.todo.R
import com.tristang.todo.taskList.Task
import com.tristang.todo.taskList.TaskListFragment
import kotlinx.android.synthetic.main.activity_task.*
import java.io.Serializable
import java.util.*

class TaskActivity : AppCompatActivity() {

    companion object {
        val TASK_KEY = "100"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        val dataid :String? = intent.extras?.get("Id").toString()


        val data = intent.extras
        if (data != null) {
            val datatitle = data.get("Title")
            add_title.setText(datatitle.toString())
            val datadesc = data.get("Description")
            add_description.setText(datadesc.toString())
        }

        add_task_btn.setOnClickListener {
            val task = Task(id = dataid ?: UUID.randomUUID().toString(), title = add_title.text.toString(), description =  add_description.text.toString())
            val intent = Intent(this, TaskListFragment::class.java)
            intent.putExtra(TASK_KEY, task as Serializable)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


}
