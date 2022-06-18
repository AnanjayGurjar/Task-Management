package ai.ineuron.taskmanagement.ui.auth

import ai.ineuron.taskmanagement.databinding.ActivityUserProfileBinding
import ai.ineuron.taskmanagement.ui.activities.HomeActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVerifyNo.setOnClickListener {
            val intent = Intent (this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}