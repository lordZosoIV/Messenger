package ge.lordzoso.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ktx.database

//import com.google.firebase.database.FirebaseDatabase


class SignUpActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val button = findViewById<Button>(R.id.updateInfo_button)
        button.setOnClickListener{
            register()
        }
    }

    private fun register() {
        val username = findViewById<TextView>(R.id.person_name).text.toString() + "@gmail.com"
        val password = findViewById<TextView>(R.id.user_job).text.toString()
        val job = findViewById<TextView>(R.id.whatIDo).text.toString()
        Log.d(username, password)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener //failure. else is successful.
                Log.d("Asd", "yes")
                val uid  = FirebaseAuth.getInstance().uid!!
                Log.d("asdddddddddd", uid)
                val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                Log.d("ref", ref.toString())
                val user = User(uid, username, job, "https://firebasestorage.googleapis.com/v0/b/messenger-83af0.appspot.com/o/images%2Fimages%2F7020bafa-fa22-11eb-9a03-0242ac130003.png?alt=media&token=3014a64e-03af-43c4-b6c0-f06e9b67b9d6")
                ref.setValue(user)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
    }
}


