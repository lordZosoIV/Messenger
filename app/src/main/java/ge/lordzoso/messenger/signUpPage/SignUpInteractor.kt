package ge.lordzoso.messenger.signUpPage

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ge.lordzoso.messenger.homePageActivity.HomePageActivity
import ge.lordzoso.messenger.model.User

class SignUpInteractor(activity: SignUpActivity) {

    private var view: SignUpActivity = activity

    fun registerUser(username: String, password: String, job: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener //failure. else is successful.
                val uid  = FirebaseAuth.getInstance().uid!!
                val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                Log.d("ref", ref.toString())
                var user = User(uid, username, job, "https://myhero.com/images/guest/g289564/hero111025/batman%202.jpg")
                ref.setValue(user).addOnFailureListener{
                    user = User(uid, username, job,
                        "https://myhero.com/images/guest/g289564/hero111025/batman%202.jpg")
                }
               view.goTo(HomePageActivity::class.java)
            }
    }


}