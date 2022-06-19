package ai.ineuron.taskmanagement.modals

import android.net.Uri

data class User(
    var userId: String? = null,
    var name: String? = null,
    var imagUrl : String? = null,
    var contact: String? = null,
    var pts: Int = 0,
    var assignedTasks: List<String> = listOf("")
    //will contain the array of all the group id the user is part of
)
