package ai.ineuron.taskmanagement.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ai.ineuron.taskmanagement.databinding.ActivityLoginBinding
import android.content.Intent
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVerifyNo.setOnClickListener {

                if(binding.etPhoneNumber.editText!!.text.isDigitsOnly()){

                    MaterialAlertDialogBuilder(this).apply {
                        setMessage("We will be Verifying ${binding.etPhoneNumber.editText!!.text} , is it ok?")
                        setPositiveButton("OK"){_,_ ->
                            val phoneNo = "+91${binding.etPhoneNumber.editText!!.text}"
                            val intent = Intent( this@LoginActivity, OtpVerification::class.java)
                            intent.putExtra("PHONE", phoneNo)
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("Cancel"){dialog,_->
                            dialog.dismiss()
                        }
                        setCancelable(false)
                        create()
                        show()
                    }


                }else{
                    Toast.makeText(this, "Invalid number, please try again", Toast.LENGTH_SHORT).show()
                }

        }


    }


}