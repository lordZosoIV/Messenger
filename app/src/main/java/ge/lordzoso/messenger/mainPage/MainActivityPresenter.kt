package ge.lordzoso.messenger.mainPage

import com.google.firebase.auth.FirebaseAuth
import ge.lordzoso.messenger.homePageActivity.HomePageActivity

class MainActivityPresenter(activity: MainActivity) : IMainActivityPresenter {

    private var view: MainActivity = activity
    private var interactor: MainPageInteractor = MainPageInteractor(activity)


    init {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            view.initView()
        } else {
            view.goTo(HomePageActivity::class.java)
        }
    }


    override fun login(nickname: String, password: String) {
        interactor.login(nickname, password)
    }

}