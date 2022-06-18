package ai.ineuron.taskmanagement.worker

import ai.ineuron.taskmanagement.MainActivity
import ai.ineuron.taskmanagement.repository.NotificationRepository
import ai.ineuron.taskmanagement.ui.TaskFragment
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationWorker(private val context : Context, params : WorkerParameters) : Worker(context, params) {
    //jab bhi humara work manager call hoga woh iss method ko call karega
    private val TAG = "Notification"

    override fun doWork(): Result {

        Log.d(TAG, "worker called doWork()")

//        val repository = (context as TaskFragment).notificationRepository
        val repository = NotificationRepository(context)

        CoroutineScope(Dispatchers.IO).launch {
            repository.createNotification()
        }

        return Result.success()
    }

}