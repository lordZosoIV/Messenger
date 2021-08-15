package ge.lordzoso.messenger.homePageActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import ge.lordzoso.messenger.R
import ge.lordzoso.messenger.UserSearchActivity
import ge.lordzoso.messenger.model.Loader
import ge.lordzoso.messenger.model.Message
import ge.lordzoso.messenger.model.User
import ge.lordzoso.messenger.userInfoPage.UserInfoPageActivity
import ge.lordzoso.messenger.utils.Utils


@Suppress("UNCHECKED_CAST")
class HomePageActivity : AppCompatActivity(), IHomePageActivityView {
    val adapter = GroupAdapter<GroupieViewHolder>()
    var presenter: IHomePagePresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = HomePagePresenter(this)
        listenForLatestMessages()
        searchUserChat()
        findViewById<RecyclerView>(R.id.searched_users).adapter = adapter
    }


    override fun initView() {

        setContentView(R.layout.activity_home_page)

        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))


        findViewById<FloatingActionButton>(R.id.fab_button).setOnClickListener {
            goTo(UserSearchActivity::class.java)
        }


        hideKeyboard(findViewById<BottomAppBar>(R.id.bottomAppbar))
        setBottomUpBarListeners()
        setSearchTextViewListener()
        sleep()
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

    override fun clearAdapter() {
        adapter.clear()
    }

    override fun adapterAdd(latestMessageRow: LatestMessageRow) {
        adapter.add(latestMessageRow)
    }

    override fun setAdapter() {

    }

    override fun adapterSetListener(currentUser: String) {
        adapter.setOnItemClickListener { item, view ->
            presenter?.onRowItemClick(item as Item<com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder>,
                view)

        }
    }

    override fun putIntent(targetUser: User, intent: Intent) {
        intent.putExtra(TARGET_USER, targetUser)
        intent.putExtra(PREV_ACTIVITY, ACTIVITY)
        startActivity(intent)
    }


    private fun setBottomUpBarListeners() {
        findViewById<ActionMenuItemView>(R.id.settings).setOnClickListener {
            val intent = Intent(this, UserInfoPageActivity::class.java)
            startActivity(intent)
        }

        findViewById<BottomAppBar>(R.id.bottomAppbar).navigationIcon?.setColorFilter(Color.BLUE,
            PorterDuff.Mode.SRC_ATOP)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSearchTextViewListener() {
        findViewById<TextView>(R.id.search_text).setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                findViewById<BottomAppBar>(R.id.bottomAppbar).visibility = View.INVISIBLE
                findViewById<FloatingActionButton>(R.id.fab_button).visibility = View.INVISIBLE
                findViewById<BottomAppBar>(R.id.bottomAppbar).navigationIcon?.setVisible(false,
                    true)
                findViewById<ActionMenuItemView>(R.id.settings).visibility = View.INVISIBLE
                return v?.onTouchEvent(event) ?: true
            }
        })

    }

    private fun searchUserChat() {
        findViewById<TextView>(R.id.search_text).setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activeBottoms()
                handleSearch()
                return@OnKeyListener true
            }
            false
        })

        findViewById<Button>(R.id.search_btn).setOnClickListener {
            activeBottoms()
            handleSearch()
        }

    }

    private fun handleSearch() {
        val usernameToSearch = findViewById<TextView>(R.id.search_text).text.toString()
        adapter.clear()
        presenter?.searchUser(usernameToSearch)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun activeBottoms() {
        hideKeyboard(findViewById<BottomAppBar>(R.id.bottomAppbar))
        findViewById<BottomAppBar>(R.id.bottomAppbar).visibility = View.VISIBLE
        findViewById<FloatingActionButton>(R.id.fab_button).visibility = View.VISIBLE
        findViewById<BottomAppBar>(R.id.bottomAppbar).navigationIcon?.setVisible(true,
            true)
        findViewById<ActionMenuItemView>(R.id.settings).visibility = View.VISIBLE
    }


    companion object {
        val TARGET_USER = "TARGET_USER"
        val PREV_ACTIVITY = "PREV_ACTIVITY"
        val ACTIVITY = "HOME_PAGE_ACTIVITY"
    }


    private fun listenForLatestMessages() {
        presenter?.listenForMessages()
    }

}


class LatestMessageRow(val message: Message, val user: User) :
    Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }



    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
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
            ).into(viewHolder.itemView.findViewById(R.id.new_message_avatar))
        val time = Utils().getTimeAgo(message.time)
        viewHolder.itemView.findViewById<TextView>(R.id.new_message_time).text =
            time.toString()


    }

}