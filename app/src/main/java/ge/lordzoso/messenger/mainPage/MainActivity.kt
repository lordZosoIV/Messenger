package ge.lordzoso.messenger.mainPage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ge.lordzoso.messenger.R
import ge.lordzoso.messenger.signUpPage.SignUpActivity

class MainActivity : AppCompatActivity(), IMainActivityView {
    private var presenter: IMainActivityPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = MainActivityPresenter(this)

    }


    override fun goTo(nextClass: Class<*>) {
        val intent = Intent(this, nextClass)
        startActivity(intent)
    }

    override fun initView() {
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.updateInfo_button)
        button.setOnClickListener {
            login()
        }

        val button2 = findViewById<Button>(R.id.sign_up_button)
        button2.setOnClickListener {
            goTo(SignUpActivity::class.java)
        }
    }

    override fun login() {
        val nickname = findViewById<TextView>(R.id.person_name).text.toString() + "@gmail.com"
        var password = findViewById<TextView>(R.id.user_job).text.toString()
        if (password.isEmpty()) password = "error"
        presenter?.login(nickname, password)
    }
}

