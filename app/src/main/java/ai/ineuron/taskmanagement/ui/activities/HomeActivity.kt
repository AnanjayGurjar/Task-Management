package ai.ineuron.taskmanagement.ui.activities

import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.databinding.ActivityHomeBinding
import ai.ineuron.taskmanagement.databinding.ActivityUserProfileBinding
import ai.ineuron.taskmanagement.ui.TaskFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addFragmentToActivity(supportFragmentManager, TaskFragment(), binding.container.id)
    }

    fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment?, frameId: Int) {
        val transaction: FragmentTransaction = manager.beginTransaction()
        if (fragment != null) {
            transaction.add(frameId, fragment)
        }
        transaction.commit()
    }
}