package ge.lordzoso.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shashank.sony.fancytoastlib.FancyToast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_row_l.view.*
import org.w3c.dom.Text


class ChatActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        findViewById<RecyclerView>(R.id.user_to_user_chat_rv).adapter = adapter

        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))

        initChatHead()

        listenForMessages()

        setUpMessageSending()

        findViewById<Button>(R.id.back).setOnClickListener{
            Log.d("aba", "Aba")
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

    private fun initChatHead() {
        val user = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)!!
        val nickname = user.nickname
        val job = user.job
        val url = user.photoUrl
        Log.d(nickname, job)

        findViewById<TextView>(R.id.chat_nickname).text = nickname.substring(0, nickname.indexOf('@',0, false))
        findViewById<TextView>(R.id.chat_job).text = job
        Glide.with(this).load(
           url
        ).into(findViewById(R.id.chat_photo))


    }


    private fun listenForMessages() {
        val from = FirebaseAuth.getInstance().uid
        val to = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$from/$to")
        val c = this

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)

                if (chatMessage != null) {
                    Loader().sleep(findViewById<ProgressBar>(R.id.progressBar))
                    if (chatMessage.from == from) {
                        adapter.add(ChatR(chatMessage.text, Utils().getTimeAgo(chatMessage.time)))
                    } else {
                        adapter.add(ChatL(chatMessage.text, Utils().getTimeAgo(chatMessage.time)))
                    }
                    findViewById<NestedScrollView>(R.id.nestedScrollView).fullScroll(View.FOCUS_DOWN)
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


        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(error: DatabaseError) {
                FancyToast.makeText(c,"No Data", FancyToast.LENGTH_LONG, FancyToast.INFO,false).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists())
                    FancyToast.makeText(c,"No Messages Yet. Start Chat", FancyToast.LENGTH_LONG, FancyToast.INFO,false).show()
                return
            }
        })

    }






    private fun setUpMessageSending() {
        findViewById<Button>(R.id.send_button_chat_log).setOnClickListener{
            Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))
            findViewById<NestedScrollView>(R.id.nestedScrollView).fullScroll(View.FOCUS_DOWN)
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
//                hideKeyboard(findViewById<TextView>(R.id.text_for_send))
                findViewById<NestedScrollView>(R.id.nestedScrollView).fullScroll(View.FOCUS_DOWN)
                Loader().sleep(findViewById<ProgressBar>(R.id.progressBar))
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


class ChatL(val text: String, val time: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.sent_message_textView).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.new_message_time).text = time
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_l
    }
}

class ChatR(val text: String, val time: String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.sent_message_textView).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.new_message_time).text = time
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_r
    }
}