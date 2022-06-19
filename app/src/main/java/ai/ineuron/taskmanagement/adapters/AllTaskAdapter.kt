package ai.ineuron.taskmanagement.adapters

import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.modals.Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllTaskAdapter(var list: List<Task>, private val listener : (Int, String) -> Unit): RecyclerView.Adapter<AllTaskAdapter.TasksViewHolder>() {

    inner class TasksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var title = itemView.findViewById<TextView>(R.id.title)
        var description = itemView.findViewById<TextView>(R.id.description)
        var status = itemView.findViewById<TextView>(R.id.status)

        init {
            itemView.setOnClickListener {
                listener.invoke(position, list[position].taskId!!)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_view , parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        var currItem = list[position]
        holder.title.text = currItem.title
        holder.description.text = currItem.description
        holder.status.text =
            if(currItem.isCompleted){
                "Completed"
        }else{
            "Active"
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}