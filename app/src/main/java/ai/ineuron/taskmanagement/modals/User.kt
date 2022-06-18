package ai.ineuron.taskmanagement.modals

data class User(
    var userId: String,
    var name: String,
    var contact: String,
    var photo: String,
    var pts: Int,
    var assignedTasks: Array<String>
    //will contain the array of all the group id the user is part of
)
