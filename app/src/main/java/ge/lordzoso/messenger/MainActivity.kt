package ge.lordzoso.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            initView()
        }else{
            goToHomePage()
        }
//

    }

    private fun goToHomePage() {
        val intent = Intent(this, HomePageActivity::class.java)
        startActivity(intent)
    }

    private fun initView() {
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.updateInfo_button)
        button.setOnClickListener{
            login()
        }

        val button2 = findViewById<Button>(R.id.sign_up_button)
        button2.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val nickname = findViewById<TextView>(R.id.person_name).text.toString() + "@gmail.com"
        var password = findViewById<TextView>(R.id.user_job).text.toString()
        if(password.isEmpty()) password = "error"
        Log.d(nickname, password)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(nickname, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    FancyToast.makeText(this,"error in login",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                    return@addOnCompleteListener
                }
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }
    }
}
