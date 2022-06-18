package ai.ineuron.taskmanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.databinding.FragmentHomeBinding
import android.content.Context
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding

    lateinit var preferances : SharedPreferences
    lateinit var editor : SharedPreferences.Editor

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

        binding.addTask.setOnClickListener {
            showDialog()
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
            val title = dialog.findViewById<EditText>(R.id.et_title)
            val description = dialog.findViewById<EditText>(R.id.et_title)

            if(validate(title, description)){
                editor
                // TODO: Call function in viewmodel to save the task in db
            }else{
                Toast.makeText(requireContext(), "Please fill the required Details", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<TextView>(R.id.negative_button).setOnClickListener{
            dialog.dismiss()
        }

        dialog.show ()
    }

    private fun validate(title: EditText, description: EditText): Boolean {
        var flag = true

        if (title.text.toString().isNullOrBlank() || description.text.toString().isNullOrBlank()){
            flag = false
        }
        return flag
    }


}