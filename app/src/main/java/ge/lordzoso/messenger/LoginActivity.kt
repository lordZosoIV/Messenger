package ge.lordzoso.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<FloatingActionButton>(R.id.fab_button).setOnClickListener{
            Log.d("fab", "Gab")
            val intent = Intent(this, UserSearchActivity::class.java)
            startActivity(intent)
        }
    }
}