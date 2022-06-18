package ai.ineuron.taskmanagement.auth

import ai.ineuron.taskmanagement.MainActivity
import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.databinding.ActivityLoginBinding
import ai.ineuron.taskmanagement.databinding.ActivityOtpVerificationBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpVerification : AppCompatActivity() {
    private lateinit var binding: ActivityOtpVerificationBinding

    val TAG = "verifynumber"
    lateinit var mCountDownTimer: CountDownTimer
    lateinit var phoneNo: String
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNo = intent.getStringExtra("PHONE").toString()
        binding.tvVerifyOtp.text = getString(R.string.otpSent_changeNumber, phoneNo)
        startVerification()
        showTimer(60000)
        verifyPhone()

        binding.btnVerifyOtp.setOnClickListener {
            val code = binding.etOtp.text.toString()
            if (code.isNotEmpty() && !(storedVerificationId.isNullOrEmpty())) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
                signInWithPhoneAuthCredential(credential)
            }
        }
        binding.btnResendOtp.setOnClickListener {
            binding.btnResendOtp.isEnabled = false
            if (resendToken != null) {
                showTimer(60000)
                callbacks?.let {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNo,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        it,
                        resendToken
                    )
                }

            }
        }

    }

    private fun verifyPhone() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNo)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun startVerification() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val smsCode = credential.smsCode
                if (smsCode != null) {
                    binding.etOtp.setText(smsCode)
                }
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(applicationContext, "Invalid request", Toast.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(
                        applicationContext,
                        "Too many requests from a single device, please try again after some time",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(TAG, "onVerificationFailed: ${e.message}",)
                    Toast.makeText(applicationContext, "Check the otp", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this, UserProfile::class.java).putExtra("PHONE", phoneNo)
                    startActivity(intent)
                    finish()

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun showTimer(milliSec: Long) {
        binding.tvOtpTimer.visibility = View.VISIBLE
        mCountDownTimer = object : CountDownTimer(milliSec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.btnResendOtp.isEnabled = false
                binding.tvOtpTimer.text =
                    "Otp sent, kindly wait for: ${millisUntilFinished / 1000} sec"
            }

            override fun onFinish() {
                binding.btnResendOtp.isEnabled = true
                binding.tvOtpTimer.visibility = View.GONE
            }
        }.start()


    }
}
