package ge.lordzoso.messenger.homePageActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shashank.sony.fancytoastlib.FancyToast
import ge.lordzoso.messenger.model.Message
import ge.lordzoso.messenger.model.User
import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import ge.lordzoso.messenger.chatPage.ChatActivity

@Suppress("UNCHECKED_CAST")
class HomePageInteractor(activity: HomePageActivity) {

    private var view: HomePageActivity = activity
    val latestMessagesMap = HashMap<String, Message>()


    fun searchUser(usernameToSearch: String) {
        view.goLive()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val c = view
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists())
                    FancyToast.makeText(c,
                        "No Such User",
                        FancyToast.LENGTH_LONG,
                        FancyToast.INFO,
                        false).show()
                dataSnapshot.children.forEach {
                    val user = User(it.value as HashMap<String, String>)
                    val nickname = user.nickname.substring(
                        0, user.nickname.indexOf(
                            '@',
                            0,
                            false
                        )
                    )
                    if (nickname == usernameToSearch) {
                        val to = user.uid
                        getLatestMessage(FirebaseAuth.getInstance().uid!!, to)
                        view.clearAdapter()
                        view.sleep()
                    }
                }
                view.sleep()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                view.sleep()
            }
        })
    }


    private fun getLatestMessage(fromId: String, toId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/last-messages/$fromId/$toId")
        latestMessagesMap.clear()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                view.goLive()
                val message = dataSnapshot.getValue(Message::class.java) ?: return
                latestMessagesMap[dataSnapshot.key!!] = message
                refreshRecyclerViewMessages(message)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", databaseError.message)
                view.sleep()
            }
        })

    }

    private fun refreshRecyclerViewMessages(message: Message) {
        view.clearAdapter()
        latestMessagesMap.values.forEach {

            val currentUser = FirebaseAuth.getInstance().uid!!
            val uid = if (currentUser == message.from) {
                message.to
            } else {
                message.from
            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("CheckResult")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)!!
                    view.adapterAdd(LatestMessageRow(it, user))
                    view.adapterSetListener(currentUser)
                    view.sleep()
                }

                override fun onCancelled(error: DatabaseError) {
                    view.sleep()
                }

            })

        }
    }

    fun onRowItemClick(item: Item<GroupieViewHolder>, v: View) {
        val intent = Intent(v.context, ChatActivity::class.java)
        val singleItem = item as LatestMessageRow
        val currentUser = FirebaseAuth.getInstance().uid!!
        val uid = if (currentUser == singleItem.message.from) {
            singleItem.message.to
        } else {
            singleItem.message.from
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("CheckResult")
            override fun onDataChange(snapshot: DataSnapshot) {
                val targetUser = snapshot.getValue(User::class.java)!!
                view.putIntent(targetUser, intent)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    fun listenMessages() {
        view.goLive()
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/last-messages/$fromId")
        val c = view
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                if (!p0.exists()) {
                    FancyToast.makeText(c,
                        "sdasdas in login",
                        FancyToast.LENGTH_LONG,
                        FancyToast.ERROR,
                        false).show()
                }
                val chatMessage = p0.getValue(Message::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages(chatMessage)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages(chatMessage)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                FancyToast.makeText(c, "No Data", FancyToast.LENGTH_LONG, FancyToast.INFO, false)
                    .show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists())
                    FancyToast.makeText(c,
                        "No Data",
                        FancyToast.LENGTH_LONG,
                        FancyToast.INFO,
                        false).show()
                return
            }
        })
    }


}