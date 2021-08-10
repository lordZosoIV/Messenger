package ge.lordzoso.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button = findViewById<Button>(R.id.sign_in)
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
        val nickname = findViewById<TextView>(R.id.editTextTextPersonName).text.toString() + "@gmail.com"
        val password = findViewById<TextView>(R.id.editTextTextPassword).text.toString()
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
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
    }
}
