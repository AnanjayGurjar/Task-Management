package ai.ineuron.taskmanagement.ui.auth

import ai.ineuron.taskmanagement.R
import ai.ineuron.taskmanagement.ui.activities.HomeActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

class Splash : AppCompatActivity() {
    var currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        hide status bar..
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash)

//        val actionBar = supportActionBar
//        actionBar!!.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            if(currentUser == null){
                val mainIntent = Intent(this, IntroActivity::class.java)
                startActivity(mainIntent)
                finish()
            }else{
                val mainIntent = Intent(this, HomeActivity::class.java)
                startActivity(mainIntent)
                finish()
            }

        }, 3000)
    }
}