package ge.lordzoso.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val password = findViewById<TextView>(R.id.user_job).text.toString()
        Log.d(nickname, password)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(nickname, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
//                    Toast.makeText(this, "invalid nickname or password", Toast.LENGTH_LONG).apply {
//                        setGravity(Gravity.TOP, 0, 0)
//                        show()
//                    }
                    return@addOnCompleteListener
                } //failure. else is successful
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }
    }
}
