package ai.ineuron.taskmanagement.adapters;

import ai.ineuron.taskmanagement.R
import android.view.View;
import java.util.List;
import ai.ineuron.taskmanagement.modals.User;
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AllUserAdapter(var list:List<User>, private val listener : (String) -> Unit): RecyclerView.Adapter <AllUserAdapter.UserViewHolder>() {

        inner class UserViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){

            val name = itemView.findViewById<TextView>(R.id.name)
            val phoneNum = itemView.findViewById<TextView>(R.id.phoneNum)
            val profilePic = itemView.findViewById<ImageView>(R.id.profilePic)

            init {
                itemView.setOnClickListener {
                    listener.invoke( list[position].userId!!)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_view , parent, false)
        return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
                var currItem = list[position]
                holder.name.text = currItem.name
//                holder.phoneNum.text = currItem.phoneNum
//                holder..text =
        }

        override fun getItemCount(): Int {
                return list.size
        }
}