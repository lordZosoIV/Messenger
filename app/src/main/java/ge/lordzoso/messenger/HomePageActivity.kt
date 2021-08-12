package ge.lordzoso.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

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

//        setUpDummyMSG()
        listenForLatestMessages()
    }

    val latestMessagesMap = HashMap<String, Message>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/last-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
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


class LatestMessageRow(val chatMessage:Message): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val uid = chatMessage.to
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("CheckResult")
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)!!
                viewHolder.itemView.findViewById<TextView>(R.id.new_message_nickname).text = user.nickname.substring(0, user.nickname.indexOf('@',0, false))
                viewHolder.itemView.findViewById<TextView>(R.id.new_message_msg).text = chatMessage.text
                Glide.with(viewHolder.itemView.findViewById<CircleImageView>(R.id.new_message_avatar)).load(user.photoUrl).into(viewHolder.itemView.findViewById(R.id.new_message_avatar));
                Log.d("data changed", "1yyyyyyyy")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
//        viewHolder.itemView.findViewById<TextView>(R.id.new_message_msg).text = chatMessage.text
    }
}
