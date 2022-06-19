package ai.ineuron.taskmanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.databinding.FragmentHomeBinding
import ai.ineuron.taskmanagement.modals.Task
import ai.ineuron.taskmanagement.modals.TimeLine
import ai.ineuron.taskmanagement.ui.viewmodel.AppViewModel
import ai.ineuron.taskmanagement.utils.AppConstants.Companion.TASK_COLLECTION
import ai.ineuron.taskmanagement.utils.NetworkResponse
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    var db = FirebaseFirestore.getInstance()
    lateinit var preferances : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    private val TAG = "HomeFragment"
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(AppViewModel::class.java)
    }
    lateinit var task : Task
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initPrefs()
        binding= FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var taskId = arguments?.getString("id")

        db.collection(TASK_COLLECTION)
            .document(taskId!!)
            .get()
            .addOnSuccessListener { document ->

                task = document.toObject(Task::class.java)!!
                binding.title.text = task!!.title
                binding.description.text = task!!.description


            }
            .addOnFailureListener { exception ->
            }
        binding.addTaskBtn.setOnClickListener {
            showDialog()
        }
        binding.reminderBtn.setOnClickListener {

        }
        binding.btnDeleteTask.setOnClickListener {

        }
        binding.btnMarkComplete.setOnClickListener {
            viewModel.updateTaskStatus(task, !task.isCompleted)
        }
    }

    private fun initPrefs() {
        preferances = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)!!
        editor = preferances.edit()
    }

    private fun showDialog () {
        val dialog = MaterialDialog(requireContext())
            .noAutoDismiss()
            .customView(R.layout.dialog_layout)

        dialog.findViewById<TextView>(R.id.positive_button).setOnClickListener{
            Log.d(TAG, "showDialog: ")
            val title = dialog.findViewById<EditText>(R.id.et_title).text.toString()
            val description = dialog.findViewById<EditText>(R.id.et_title).text.toString()

            if(validate(title, description)){
                editor
                // TODO: Call function in viewmodel to save the task in db
                var timeline = TimeLine("5", "10")
                val taskId = UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault())
//                val task = viewModel.getuser(FirebaseAuth.getInstance().currentUser)?.let { it1 ->
//                    Log.d(TAG, "showDialog: user is not null")
//                    Task(taskId, false, title.toString(), description.toString(),
//                        it1,  timeline, listOf())
//                }
                var task : Task? = null
                viewModel.getuser(FirebaseAuth.getInstance().currentUser)
                viewModel.liveUser.observe(viewLifecycleOwner){
                    task = Task(taskId, false, title.toString(), description.toString(),
                        it,  timeline, listOf(it.userId!!))
                    Log.d(TAG, "showDialog: task object is " + task) ///coming null
                    viewModel.pushTaskToDb(task!!)
                }

            }else{
                Toast.makeText(requireContext(), "Please fill the required Details", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener{
            dialog.dismiss()
        }

        dialog.show ()
    }

    private fun validate(title: String, description: String): Boolean {
        var flag = true

        if (title.isNullOrBlank() || description.isNullOrBlank()){
            flag = false
        }
        return flag
    }


}