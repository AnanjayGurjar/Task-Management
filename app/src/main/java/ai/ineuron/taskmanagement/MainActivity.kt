package ai.ineuron.taskmanagement

import ai.ineuron.taskmanagement.databinding.ActivityMainBinding
import ai.ineuron.taskmanagement.ui.HomeFragment
import ai.ineuron.taskmanagement.ui.TaskFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

val TOPIC = "/topics/myTask"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
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