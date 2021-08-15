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
        var password = findViewById<TextView>(R.id.user_job).text.toString()
        var job = findViewById<TextView>(R.id.whatIDo).text.toString()
        if(password.isEmpty()) password = "error"
        if(job.isEmpty()) job = "error"
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener //failure. else is successful.
                val uid  = FirebaseAuth.getInstance().uid!!
                val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                Log.d("ref", ref.toString())
                var user = User(uid, username, job, "https://firebasestorage.googleapis.com/v0/b/messenger-83af0.appspot.com/o/images%2F004bf43a-fd5e-11eb-9a03-0242ac130003.png?alt=media&token=d1ec4322-7f3f-4485-9bf6-f56ebca09078")
                ref.setValue(user).addOnFailureListener{
                    user = User(uid, username, job,
                        "https://myhero.com/images/guest/g289564/hero111025/batman%202.jpg")
                }
                val intent = Intent(this, HomePageActivity::class.java)
                startActivity(intent)
            }
    }
}


