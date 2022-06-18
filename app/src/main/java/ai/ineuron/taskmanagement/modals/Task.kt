package ai.ineuron.taskmanagement.modals

data class Task(
    var taskId: String,
    var isCompleted: Boolean = false,
    var title: String? = null,
    var description: String? = null,
    var createdBy: User,
    var timeline: TimeLine,
    var allUsers: Array<String>,
    //will contain the array of id of all the user the task is alloted to

)
