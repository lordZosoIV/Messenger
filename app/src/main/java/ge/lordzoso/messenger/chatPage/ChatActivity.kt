package ge.lordzoso.messenger.chatPage

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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.lordzoso.messenger.*
import ge.lordzoso.messenger.homePageActivity.HomePageActivity
import ge.lordzoso.messenger.model.Loader
import ge.lordzoso.messenger.model.User


class ChatActivity : AppCompatActivity(), IChatActivityView {
    val adapter = GroupAdapter<GroupieViewHolder>()
    private var presenter: IChatPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ChatPresenter(this)
        listenForMessages()


    }


    override fun goTo(nextClass: Class<*>) {
        val intent = Intent(this, nextClass)
        startActivity(intent)
    }

    override fun goLive() {
        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))
    }

    override fun sleep() {
        Loader().sleep(findViewById<ProgressBar>(R.id.progressBar))
    }

    override fun initView() {
        setContentView(R.layout.activity_chat)

        findViewById<RecyclerView>(R.id.user_to_user_chat_rv).adapter = adapter

        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))

        initChatHead()

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

    override fun chatRAdd(chatR: ChatR) {
        adapter.add(chatR)
    }

    override fun chatLAdd(chatL: ChatL) {
        adapter.add(chatL)
    }

    override fun scrollDown() {
        findViewById<NestedScrollView>(R.id.nestedScrollView).fullScroll(View.FOCUS_DOWN)
    }

    private fun initChatHead() {
        val user = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)!!
        val nickname = user.nickname
        val job = user.job
        val url = user.photoUrl

        findViewById<TextView>(R.id.chat_nickname).text = nickname.substring(0, nickname.indexOf('@',0, false))
        findViewById<TextView>(R.id.chat_job).text = job
        Glide.with(this).load(
           url
        ).into(findViewById(R.id.chat_photo))


    }


    override fun listenForMessages() {
        presenter?.listenMessages(intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)!!)
    }

    @SuppressLint("CutPasteId")
    override fun clear() {
        findViewById<TextView>(R.id.text_for_send).text = ""
        findViewById<TextView>(R.id.text_for_send).hint = resources.getString(R.string.message)
        findViewById<NestedScrollView>(R.id.nestedScrollView).fullScroll(View.FOCUS_DOWN)
        Loader().sleep(findViewById<ProgressBar>(R.id.progressBar))
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
        val to = intent.getParcelableExtra<User>(UserSearchActivity.TARGET_USER)?.uid

        presenter?.sendMessage(msg, to)
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