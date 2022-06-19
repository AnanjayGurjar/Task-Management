package ai.ineuron.taskmanagement.ui.viewmodel

import ai.ineuron.taskmanagement.modals.User
import ai.ineuron.taskmanagement.modals.Task
import ai.ineuron.taskmanagement.utils.AppConstants
import ai.ineuron.taskmanagement.utils.AppConstants.Companion.TASK_COLLECTION
import ai.ineuron.taskmanagement.utils.AppConstants.Companion.USER_COLLECTION
import ai.ineuron.taskmanagement.utils.NetworkResponse
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.Continuation
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

private const val TAG = "AppViewModel"
class AppViewModel(application: Application) : AndroidViewModel(application) {

    val db = Firebase.firestore

    var allUsers = MutableLiveData<List<User>>()
    private val _allUsers = ArrayList<User>()

    var allTasks = MutableLiveData<List<Task>>()
    private val _allTasks = ArrayList<Task>()

    var assignedTasksToUser = MutableLiveData<List<Task>>()
    private val _assignedTasksToUser = ArrayList<Task>()

    var userResponse: MutableLiveData<NetworkResponse<User>> = MutableLiveData()
    var taskResponse: MutableLiveData<NetworkResponse<Task>> = MutableLiveData()

    private lateinit var storageReference: StorageReference

    var liveUser : MutableLiveData<User> = MutableLiveData()

    fun pushUserToDb(user: User, collectionName: String = USER_COLLECTION){
        Log.d(TAG, "push function called")
        viewModelScope.launch {
            db.collection(collectionName).document(user.userId!!)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error writing document", e)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getAllUsers(collectionName: String = USER_COLLECTION){
        userResponse.value = NetworkResponse.Loading()
        viewModelScope.launch {
            db.collection(collectionName)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val user = document.toObject(User::class.java)
                        _allUsers.add(user)
                    }
                    userResponse.value = NetworkResponse.Success()
                    allUsers.postValue(_allUsers)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                    userResponse.value = NetworkResponse.Failure(exception.message.toString())
                }
        }
    }

    fun pushTaskToDb(task: Task, collectionName: String = TASK_COLLECTION){
        viewModelScope.launch {
            db.collection(collectionName).document(task.taskId!!)
                .set(task)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error writing document", e)
                }
        }
    }
    
    fun getAllTasks(collectionName: String = TASK_COLLECTION){
        taskResponse.value = NetworkResponse.Loading()
        viewModelScope.launch {
            db.collection(collectionName)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val user = document.toObject(Task::class.java)
                        _allTasks.add(user)
                    }
                    taskResponse.value = NetworkResponse.Success()
                    allTasks.postValue(_allTasks)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                    taskResponse.value = NetworkResponse.Failure(exception.message.toString())
                }
        }
    }


    fun getuser(user: FirebaseUser?) {

        val id = user?.uid.toString()
        Log.d(TAG, "getuser: inside viewModal id to find is " + id)
        db.collection(USER_COLLECTION)
            .whereEqualTo("userId", id)
            .get()
            .addOnSuccessListener { users ->
                Log.d(TAG, "getuser: inside viewModal users are " + users)
                for (myUser in users){
                    var muser = myUser.toObject(User::class.java)
                    liveUser.postValue(muser)
                }
                userResponse.value = NetworkResponse.Success()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                userResponse.value = NetworkResponse.Failure(exception.message.toString())
            }
    }

    fun addNewUserToTask(task: Task, userToAdd: User, collectionName: String = TASK_COLLECTION){
        viewModelScope.launch {
            val taskRef = db.collection(collectionName).document(task.taskId!!)
            taskRef.update("allUsers", FieldValue.arrayUnion(userToAdd.userId))
                .addOnSuccessListener {
                    Log.d(TAG, "addNewUserToTask: User ${userToAdd.name} is added to ${task.title}")
                }
                .addOnFailureListener {
                    Log.d(TAG, "addNewUserToTask: Failed to add user to task ${it.printStackTrace()}")
                }
        }
    }
    
    fun assignTaskToUser(user: User, taskToBeAssigned: Task, collectionName: String = USER_COLLECTION){
        viewModelScope.launch {
            val taskRef = db.collection(collectionName).document(user.userId!!)
            taskRef.update("assignedTasks", FieldValue.arrayUnion(taskToBeAssigned.taskId))
                .addOnSuccessListener {
                    Log.d(TAG, "assignTaskToUser: Task ${taskToBeAssigned.title} is assigned to ${user.name}")
                }
                .addOnFailureListener {
                    Log.d(TAG, "addNewUserToTask: Failed to add user to task ${it.printStackTrace()}")
                }
        }
    }

    fun getTasksAssignedToUser(user: User){
        userResponse.value = NetworkResponse.Loading()

        //TODO check if the user has assignedTasks locally or can call it there itself
        var taskIds = user.assignedTasks
        viewModelScope.launch {
            for (id in taskIds){
                db.collection(TASK_COLLECTION)
                    .whereEqualTo("taskId", id)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            //though there will only be one document in this documents
                            val task: Task = document.toObject(Task::class.java)
                            _assignedTasksToUser.add(task)
                        }
                        userResponse.value = NetworkResponse.Success()
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                        userResponse.value = NetworkResponse.Failure(exception.message.toString())
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
            assignedTasksToUser.postValue(_assignedTasksToUser)
        }
    }


    fun uploadImageToStorage(bitmap: Bitmap, currentUser: User){
        userResponse.value = NetworkResponse.Loading()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        var path = "$USER_COLLECTION/${currentUser.userId}/profile.jpg"
        var mStorageReference = FirebaseStorage.getInstance().reference.child(path)
        var uploadTask = mStorageReference.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e(TAG, "uploadImages: image couldnot be uploaded", it)
            userResponse.value = NetworkResponse.Failure(it.toString())

        }.addOnSuccessListener { taskSnapshot ->
            userResponse.value = NetworkResponse.Success()
            mStorageReference.downloadUrl
                .addOnSuccessListener {
                    Log.d(TAG, "uploadImages: ${it.toString()}")
                    var user = User(userId = currentUser.userId,imagUrl =  it.toString())
                    userResponse.value = NetworkResponse.Success(user)
                }
                .addOnFailureListener {
                    userResponse.value = NetworkResponse.Failure(it.message.toString())
                    Log.e(TAG, "uploadImages: Failed to get the url", )
                }
            taskSnapshot.storage
        }
    }


    fun uploadImages( fileBytes: ByteArray,  currentUser: User){
        userResponse.value = NetworkResponse.Loading()
        viewModelScope.launch {
            var mStorageReference = FirebaseStorage.getInstance().reference
            val fileToUpload = mStorageReference.child("$USER_COLLECTION/${currentUser.userId}/${currentUser.name}.jpg")
            fileToUpload.putBytes(fileBytes)
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    Log.e(TAG, "uploadImages: image couldnot be uploaded", it)
                    userResponse.value = NetworkResponse.Failure(it.toString())
                }
                .addOnCompleteListener {
                }
        }

    }
//
//    private fun getImagesAgain(uri: Uri){
//        var mStorageReference = FirebaseStorage.getInstance().reference
//        val uploadTask = uri?.let { mStorageReference.putFile(it) }
//        if (uploadTask != null) {
//            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>>{ task ->
//
//                if (!task.isSuccessful){
//                    task.exception.let {
//                        throw it!!
//                    }
//                }else{
//                    return@Continuation ref.downloadUrl
//                }
//
//            }).addOnCompleteListener {task->
//                btn_next.isEnabled = true
//                if (task.isSuccessful){
//                    downloadUrl = task.result.toString()
//                    Log.d("URL", "Download Url: $downloadUrl")
//                }
//            }.addOnFailureListener {
//                Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
//            }
//    }


    fun getImageBitmap (currentUser: User) : Bitmap? {
        userResponse.value = NetworkResponse.Loading()
        var bitmapImage : Bitmap? = null
        storageReference = FirebaseStorage.getInstance().getReference("$USER_COLLECTION/${currentUser.userId}/${currentUser.name}.jpg")
        try {
            val localFile: File = File.createTempFile("tempfile", ".jpg")
            storageReference.getFile(localFile)
                .addOnSuccessListener {
                    bitmapImage = BitmapFactory.decodeFile(localFile.absolutePath)
                    //TODO set image as binding.id.setImageBitmap+
                    userResponse.value = NetworkResponse.Success()
                }
                .addOnFailureListener{
//                    Toast.makeText(requireContext(), )
                    Log.d(TAG, "Image fetched failed: " + it.message.toString())
                    userResponse.value = NetworkResponse.Failure(it.message.toString())

                }
        }catch(e : IOException){
            e.printStackTrace()
            Log.d(TAG, "io Exception occurred")
        }
        return bitmapImage
    }

    fun deleteTask(task : Task, collectionName: String = TASK_COLLECTION){
        db.collection(collectionName).document(task.taskId!!)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Task successfully deleted!")
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error deleting document", e)
            }
    }

    fun updateTaskStatus(task : Task, isCompleted : Boolean, collectionName: String = TASK_COLLECTION){
        db.collection(collectionName).document(task.taskId!!)
            .update("isCompleted", isCompleted)
            .addOnSuccessListener {
                Log.d(TAG, "Task ${task.title} is succesfully updated")
            }
            .addOnFailureListener {
                    e -> Log.e(TAG, "Error updating task", e)
            }
    }


}