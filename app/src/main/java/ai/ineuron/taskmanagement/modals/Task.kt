package ai.ineuron.taskmanagement.modals

data class Task(
    var taskId: String? = null,
    var isCompleted: Boolean = true,
    var title: String? = null,
    var description: String? = null,
    var createdBy: User? = null,
    var timeline: TimeLine? = null,
    var allUsers: List<String> = listOf(""),
    //will contain the array of id of all the user the task is alloted to

)
