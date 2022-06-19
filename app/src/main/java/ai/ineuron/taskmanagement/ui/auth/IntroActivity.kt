package ai.ineuron.taskmanagement.ui.auth

import ai.ineuron.taskmanagement.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        val btn = findViewById<MaterialButton>(R.id.btnGetStarted)
        btn.setOnClickListener {
            val mainIntent = Intent(this@IntroActivity, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

    }
}