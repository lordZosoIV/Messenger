package ge.lordzoso.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView


@Suppress("UNCHECKED_CAST")
class HomePageActivity: AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        findViewById<RecyclerView>(R.id.searched_users).adapter = adapter

        findViewById<FloatingActionButton>(R.id.fab_button).setOnClickListener{
            Log.d("fab", "Gab")
            val intent = Intent(this, UserSearchActivity::class.java)
            startActivity(intent)
        }

        listenForLatestMessages()
        setSearchTextViewListener()
        searchUserChat()
        setBottomUpBarListeners()
    }


    private fun setBottomUpBarListeners() {
        findViewById<Button>(R.id.settings).setOnClickListener{
            val intent = Intent(this, UserInfoPageActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchTextViewListener() {


        findViewById<TextView>(R.id.search_text).setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                findViewById<BottomAppBar>(R.id.bottomAppbar).visibility = View.INVISIBLE
                findViewById<FloatingActionButton>(R.id.fab_button).visibility = View.INVISIBLE

                return v?.onTouchEvent(event) ?: true
            }
        })

    }

    private fun searchUserChat() {
        findViewById<Button>(R.id.search_btn).setOnClickListener{

            val usernameToSearch = findViewById<TextView>(R.id.search_text).text.toString()
            Log.d("movdzebnot", usernameToSearch)
            val ref = FirebaseDatabase.getInstance().getReference("/users")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach {
                        Log.d("NewMessage", it.toString())
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
                            adapter.clear()
                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", databaseError.message) //Don't ignore errors!
                }
            })
        }
    }

    val latestMessagesMap = HashMap<String, Message>()

    companion object{
        val TARGET_USER = "TARGET_USER"
        val PREV_ACTIVITY = "PREV_ACTIVITY"
        val ACTIVITY = "HOME_PAGE_ACTIVITY"
    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
            adapter.setOnItemClickListener{ item, view->
                val intent = Intent(view.context, ChatActivity::class.java)
                val singleItem = item as LatestMessageRow
                val currentUser = FirebaseAuth.getInstance().uid!!
                val uid = if(currentUser == singleItem.message.from){
                    singleItem.message.to
                }else{
                    singleItem.message.from
                }
                val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("CheckResult")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val targetUser = snapshot.getValue(User::class.java)!!
                        intent.putExtra(TARGET_USER, targetUser)
                        intent.putExtra(PREV_ACTIVITY, ACTIVITY)
                        startActivity(intent)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
            }
        }
    }


    private fun getLatestMessage(fromId: String, toId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/last-messages/$fromId/$toId")
        latestMessagesMap.clear()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val message = dataSnapshot.getValue(Message::class.java) ?: return
                latestMessagesMap[dataSnapshot.key!!] = message
                refreshRecyclerViewMessages()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", databaseError.message)
            }
        })

    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/last-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}


class LatestMessageRow(val message: Message): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val currentUser = FirebaseAuth.getInstance().uid!!
        val uid = if(currentUser == message.from){
            message.to
        }else{
            message.from
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("CheckResult")
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                viewHolder.itemView.findViewById<TextView>(R.id.new_message_nickname).text =
                    user.nickname.substring(
                        0, user.nickname.indexOf(
                            '@',
                            0,
                            false
                        )
                    )
                viewHolder.itemView.findViewById<TextView>(R.id.new_message_msg).text = message.text
                Glide.with(viewHolder.itemView.findViewById<CircleImageView>(R.id.new_message_avatar))
                    .load(
                        user.photoUrl
                    ).into(viewHolder.itemView.findViewById(R.id.new_message_avatar));
                Log.d("data changed", "1yyyyyyyy")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
//        viewHolder.itemView.findViewById<TextView>(R.id.new_message_msg).text = chatMessage.text
    }
}
