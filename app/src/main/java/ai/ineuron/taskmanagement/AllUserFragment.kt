package ai.ineuron.taskmanagement

import ai.ineuron.taskmanagement.databinding.FragmentAllTaskBinding
import ai.ineuron.taskmanagement.databinding.FragmentAllUserBinding
import ai.ineuron.taskmanagement.modals.User
import ai.ineuron.taskmanagement.ui.viewmodel.AppViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

class AllUserFragment : Fragment() {


    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        ).get(AppViewModel::class.java)
    }

    private lateinit var binding: FragmentAllUserBinding
    var allUsers = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllTasks()
        viewModel.allUsers.observe(viewLifecycleOwner){
            allUsers.clear()
            allUsers.addAll(it)
        }
    }


}