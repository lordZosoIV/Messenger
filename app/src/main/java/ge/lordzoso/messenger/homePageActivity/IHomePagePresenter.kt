package ge.lordzoso.messenger.homePageActivity

import android.content.Intent
import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ge.lordzoso.messenger.model.Message

interface IHomePagePresenter {
    fun searchUser(usernameToSearch: String)
    fun listenForMessages()
    fun onRowItemClick(item: Item<GroupieViewHolder>, view: View)
    fun handleEachChildNextActivity(uid: String, intent: Intent)
}