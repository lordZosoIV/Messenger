package ge.lordzoso.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_row_l.view.*


class ChatActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        findViewById<RecyclerView>(R.id.user_to_user_chat_rv).adapter = adapter

//        val user = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)!!

//        setDummyData()

        listenForMessages()

        setUpMessageSending()

        findViewById<Button>(R.id.back).setOnClickListener{
            val prev = intent.getStringExtra("PREV_ACTIVITY")
            val c = if(prev == HomePageActivity.ACTIVITY){
                HomePageActivity::class.java
            }else{
                UserSearchActivity::class.java
            }
            val intent = Intent(this, c)
            startActivity(intent)
        }


    }


    private fun listenForMessages() {
        val from = FirebaseAuth.getInstance().uid
        val to = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$from/$to")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)

                if (chatMessage != null) {

                    if (chatMessage.from == from) {
                        adapter.add(ChatR(chatMessage.text))
                    } else {
                        adapter.add(ChatL(chatMessage.text))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }






    private fun setUpMessageSending() {
        findViewById<Button>(R.id.send_button_chat_log).setOnClickListener{
            saveMessageToDB()
        }
    }

    @SuppressLint("CutPasteId")
    private fun saveMessageToDB() {
        val msg = findViewById<TextView>(R.id.text_for_send).text.toString()
        val from = FirebaseAuth.getInstance().uid
        val to = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)?.uid

        val ref = FirebaseDatabase.getInstance().getReference("/messages/$from/$to").push()
        val refRev = FirebaseDatabase.getInstance().getReference("/messages/$to/$from").push()


        if (from == null || to == null) return

        val message = Message(ref.key!!, msg, from, to, System.currentTimeMillis() / 1000)

        ref.setValue(message).addOnSuccessListener {
            refRev.setValue(message).addOnSuccessListener {
                Log.d("message sent", message.toString())
                findViewById<TextView>(R.id.text_for_send).text = ""
                findViewById<TextView>(R.id.text_for_send).hint = resources.getString(R.string.message)
                hideKeyboard(findViewById<TextView>(R.id.text_for_send))
                findViewById<RecyclerView>(R.id.user_to_user_chat_rv).scrollToPosition(adapter.itemCount - 1)
            }
        }
        val reference = FirebaseDatabase.getInstance().getReference("/last-messages/$from/$to")
        val referenceRev = FirebaseDatabase.getInstance().getReference("/last-messages/$to/$from")
        reference.setValue(message)
        referenceRev.setValue(message)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}


class ChatL(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.sent_message_textView).text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_l
    }
}

class ChatR(val text: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.sent_message_textView).text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_r
    }
}