package ai.ineuron.taskmanagement.modals

data class Task(
    var title: String? = null,
    var description: String? = null,
    var list: List<Person>,
    var timeline: TimeLine
)
