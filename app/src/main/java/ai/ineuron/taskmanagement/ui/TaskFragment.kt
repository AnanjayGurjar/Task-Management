package ai.ineuron.taskmanagement.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.TOPIC
import ai.ineuron.taskmanagement.databinding.FragmentHomeBinding
import ai.ineuron.taskmanagement.databinding.FragmentTaskBinding
import ai.ineuron.taskmanagement.repository.NotificationRepository
import ai.ineuron.taskmanagement.worker.NotificationWorker
import android.util.Log
import android.widget.Toast
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import java.util.concurrent.TimeUnit


class TaskFragment : Fragment() {

    private lateinit var binding : FragmentTaskBinding

    private val TAG = "TaskFragment"
//    lateinit var notificationRepository: NotificationRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        notificationRepository = NotificationRepository(requireContext())
        Firebase.messaging.subscribeToTopic(TOPIC)
            .addOnCompleteListener{ task ->
                var msg = "Subscribed"
                if(!task.isSuccessful){
                    msg = "Subscribe fail"
                }
                Log.d(TAG, msg)
//                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }

        binding= FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reminderBtn.setOnClickListener {
            openTimePicker()
        }
    }

    private fun openTimePicker(){
        val timeFormat = TimeFormat.CLOCK_12H
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(timeFormat)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Set time period for reminder")
            .build()
        picker.show(parentFragmentManager, "TAG")
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val min = picker.minute
            binding.etTime.setText(
                String.format("%02d : %02d",hour, min)
            )
            binding.etTime.isFocusable = false
            setupWorker(hour, min)
        }
        picker.addOnNegativeButtonClickListener {
            picker.dismiss()
            binding.etTime.isFocusable = false
        }
    }

    private fun setupWorker(hour: Int , min: Int) {

        val minutes = (hour*60 + min).toLong()
        Log.d(TAG, minutes.toString())

        val workerRequest = PeriodicWorkRequest.Builder(NotificationWorker::class.java, minutes, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workerRequest)
    }
}