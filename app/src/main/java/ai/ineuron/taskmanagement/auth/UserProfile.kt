package ai.ineuron.taskmanagement.auth

import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.databinding.ActivityMainBinding
import ai.ineuron.taskmanagement.databinding.ActivityUserProfileBinding
import ai.ineuron.taskmanagement.ui.TaskFragment
import ai.ineuron.taskmanagement.ui.activities.HomeActivity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class UserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextContinue.setOnClickListener {
            val intent = Intent (this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}