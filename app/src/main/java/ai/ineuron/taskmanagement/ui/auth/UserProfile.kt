package ai.ineuron.taskmanagement.ui.auth


import ai.ineuron.taskmanagement.databinding.ActivityUserProfileBinding
import ai.ineuron.taskmanagement.modals.User
import ai.ineuron.taskmanagement.ui.activities.HomeActivity
import ai.ineuron.taskmanagement.ui.viewmodel.AppViewModel
import ai.ineuron.taskmanagement.utils.AppConstants
import ai.ineuron.taskmanagement.utils.NetworkResponse
import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException


private const val TAG = "UserProfile"
class UserProfile : AppCompatActivity() {

    val RESULT_IMAGE = 10
    var phoneNo: String? = ""


    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(AppViewModel::class.java)
    }

    private lateinit var binding: ActivityUserProfileBinding
    private val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore
    var bitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNo = "+916375566263"
//        phoneNo = intent.getStringExtra("PHONE")


        binding.profilePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_IMAGE)
        }

        binding.btnVerifyNo.setOnClickListener {

            if(bitmap != null && ! binding.userFullName.text.isNullOrEmpty() && ! binding.userEmail.text.isNullOrEmpty()){
                Log.d(TAG, "inside if")
                var name = binding.userFullName.text

                var user = phoneNo?.let { phoneNo -> User(user!!.uid, name.toString(), "", phoneNo,0, listOf("") ) }
                Log.d(TAG, "user object is " + user)


//                    pushUserToDb(user!!)
//                    val byteArray = compressImage(uri!!)
//                Log.d(TAG, "onCreate: $byteArray")
                if (user != null) {
                    viewModel.uploadImageToStorage(bitmap!!, user)
                }
                viewModel.userResponse.observe(this){
                    when(it){
                        is NetworkResponse.Success->{
                            var registeredUser = it.data
                            Log.d(TAG, "onCreate: ${registeredUser.toString()}")
                            user!!.imagUrl = registeredUser!!.imagUrl
                            viewModel.pushUserToDb(user!!)
                            Toast.makeText(this, "Image Uploaded SuccesFully", Toast.LENGTH_SHORT).show()

                        }
                        is NetworkResponse.Failure->{
                            Toast.makeText(this, "Failed due to ${it.error}", Toast.LENGTH_SHORT).show()
                        }
                        else->{

                        }
                    }
                }

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()

//                    uploadImages(byteArray, user)

            }else{
                Log.d(TAG, "outsside if")
                Toast.makeText(this, "please enter all the fields", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun compressImage(uri: Uri): ByteArray {
        var bmp: Bitmap? = null
        try {
            bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
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
//        var img: Bitmap? = null

        if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                val imageUri = data.data
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                binding.profilePic.setImageURI(imageUri)
            }else{
                Log.d(TAG, "onActivityResult: data is null")
            }
        }else{
            Log.d(TAG, "onActivityResult: User request code doesn't match")
        }
    }

}

