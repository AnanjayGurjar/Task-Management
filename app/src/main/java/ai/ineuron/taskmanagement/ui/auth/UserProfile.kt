package ai.ineuron.taskmanagement.ui.auth

import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.databinding.ActivityUserProfileBinding
import ai.ineuron.taskmanagement.modals.Task
import ai.ineuron.taskmanagement.modals.User
import ai.ineuron.taskmanagement.ui.viewmodel.AppViewModel
import ai.ineuron.taskmanagement.utils.AppConstants
import ai.ineuron.taskmanagement.utilshy.NetworkResponse
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.IOException

class UserProfile : AppCompatActivity() {

    val RESULT_IMAGE = 10

    private val TAG = "UserProfile"

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(AppViewModel::class.java)
    }

    private lateinit var binding : ActivityUserProfileBinding
    private val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore
    private var myUser : User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profilePic.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_IMAGE)
        }
        myUser =  viewModel.getuser(user)

        Log.d(TAG, "user of type User is : " + myUser)
    }



    private fun compressImage (uri : Uri) : ByteArray{
        var bmp: Bitmap? = null
        try {
            bmp = MediaStore.Images.Media.getBitmap(this .contentResolver,uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val baos = ByteArrayOutputStream()

        //here you can choose quality factor in third parameter(ex. i choosen 25)
        bmp!!.compress(Bitmap.CompressFormat.JPEG, 25, baos)
       return baos.toByteArray()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var img : Bitmap? = null

        if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.clipData != null) {
                    myUser?.let { img = viewModel.getImageBitmap(it) }

                    if(img != null){
                        binding.profilePic.setImageBitmap(img)
                    }
                }
            }
        }
    }
}