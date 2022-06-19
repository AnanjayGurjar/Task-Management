package ai.ineuron.taskmanagement

import ai.ineuron.taskmanagement.adapters.AllTaskAdapter
import ai.ineuron.taskmanagement.databinding.FragmentAllTaskBinding
import ai.ineuron.taskmanagement.modals.Task
import ai.ineuron.taskmanagement.ui.viewmodel.AppViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth

class AllTaskFragment : Fragment() {

    private lateinit var binding: FragmentAllTaskBinding
    var task : Task? = null

    var tasksAssignedToUser = ArrayList<Task>()

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(AppViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTaskBtn.setOnClickListener{
            findNavController().navigate(
                R.id.action_allTaskFragment_to_addTaskFragment
            )
        }

        viewModel.getuser(FirebaseAuth.getInstance().currentUser)
        viewModel.liveUser.observe(viewLifecycleOwner){
        }
        viewModel.assignedTasksToUser.observe(viewLifecycleOwner){
            tasksAssignedToUser.addAll(it)
        }
    }

    private fun initRecyclerView(){
        with(binding.taskRV){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AllTaskAdapter(tasksAssignedToUser){ position, id ->
                var bundle = bundleOf("position" to position, "id" to id)
                    findNavController().navigate(
                        R.id.action_allTaskFragment_to_homeFragment, bundle
                    )
            }
        }
    }



}