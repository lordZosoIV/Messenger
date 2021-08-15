package ge.lordzoso.messenger.homePageActivity

import android.content.Intent
import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class HomePagePresenter(activity: HomePageActivity) : IHomePagePresenter {
    private var view: HomePageActivity = activity
    private var interactor: HomePageInteractor = HomePageInteractor(activity)

    init {
        view.initView()
    }

    override fun searchUser(usernameToSearch: String) {
        interactor.searchUser(usernameToSearch)
    }

    override fun listenForMessages() {
        interactor.listenMessages()
    }

    override fun onRowItemClick(item: Item<GroupieViewHolder>, view: View) {
        interactor.onRowItemClick(item, view)
    }

    override fun handleEachChildNextActivity(uid: String, intent: Intent) {
        interactor.handleEachCHildNextActivity(uid, intent)
    }


}