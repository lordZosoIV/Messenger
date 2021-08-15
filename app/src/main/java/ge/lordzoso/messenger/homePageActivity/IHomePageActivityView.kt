package ge.lordzoso.messenger.homePageActivity

import android.content.Intent
import ge.lordzoso.messenger.model.Message
import ge.lordzoso.messenger.model.User

interface IHomePageActivityView {
    fun initView()
    fun goTo(nextClass: Class<*>)
    fun goLive()
    fun sleep()
    fun clearAdapter()
    fun adapterAdd(latestMessageRow: LatestMessageRow)
    fun setAdapter()
    fun adapterSetListener(currentUser: String)
    fun putIntent(targetUser: User, intent: Intent)
    fun refreshRecyclerViewMessages(latestMessagesMap: HashMap<String, Message>)
}