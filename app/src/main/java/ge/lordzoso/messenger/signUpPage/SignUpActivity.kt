package ge.lordzoso.messenger.signUpPage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ge.lordzoso.messenger.R

//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ktx.database

//import com.google.firebase.database.FirebaseDatabase


class SignUpActivity: AppCompatActivity(), ISignUpActivityView {

    private var presenter: ISignUpPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SignUpPresenter(this)

    }

    override fun initView() {
        setContentView(R.layout.activity_sign_up)
        val button = findViewById<Button>(R.id.updateInfo_button)
        button.setOnClickListener{
            register()
        }
    }


    override fun goTo(nextClass: Class<*>) {
        val intent = Intent(this, nextClass)
        startActivity(intent)
    }

    override fun register() {
        val username = findViewById<TextView>(R.id.person_name).text.toString() + "@gmail.com"
        var password = findViewById<TextView>(R.id.user_job).text.toString()
        var job = findViewById<TextView>(R.id.whatIDo).text.toString()
        if(password.isEmpty()) password = "error"
        if(job.isEmpty()) job = "error"
        presenter?.register(username, password, job)
    }
}


