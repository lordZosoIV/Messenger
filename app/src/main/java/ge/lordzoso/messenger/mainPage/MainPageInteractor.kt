package ge.lordzoso.messenger.mainPage

import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast
import ge.lordzoso.messenger.homePageActivity.HomePageActivity

class MainPageInteractor(activity: MainActivity) {

    private var view: MainActivity = activity

    fun login(nickname: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(nickname, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    FancyToast.makeText(view.applicationContext,"error in login",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                    return@addOnCompleteListener
                }
                view.goTo(HomePageActivity::class.java)
            }
    }


}