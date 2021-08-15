package ge.lordzoso.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ge.lordzoso.messenger.chatPage.ChatActivity
import ge.lordzoso.messenger.homePageActivity.HomePageActivity
import ge.lordzoso.messenger.model.Loader
import ge.lordzoso.messenger.model.User
import ge.lordzoso.messenger.userSearchByPatternPage.IUserSearchByPatternView
import ge.lordzoso.messenger.userSearchByPatternPage.IUserSearchPresenter
import ge.lordzoso.messenger.userSearchByPatternPage.UserSearchPresenter


@Suppress("UNCHECKED_CAST")
class UserSearchActivity : AppCompatActivity(), IUserSearchByPatternView {
    val adapter = GroupAdapter<GroupieViewHolder>()
    private var presenter: IUserSearchPresenter? = null

    @SuppressLint("ClickableViewAccessibility")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = UserSearchPresenter(this)
        fetchUsers()

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
        setContentView(R.layout.activity_user_search_by_pattern)
        goLive()

        findViewById<Button>(R.id.back).setOnClickListener {
            goTo(HomePageActivity::class.java)
        }


        findViewById<TextView>(R.id.search_text).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int,
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int,
            ) {
                val pattern = findViewById<TextView>(R.id.search_text).text.toString()
                adapter.clear()
                if (pattern.length > 3) {
                    fetchUserByPatternListener(pattern)
                }
            }
        })
    }

    override fun adapterAdd(userItem: UserItem) {
        adapter.add(userItem)
    }

    override fun setAdapter() {
        findViewById<RecyclerView>(R.id.searched_users).adapter = adapter
    }

    override fun adapterSetListener() {
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatActivity::class.java)
            val targetUser = item as UserItem
            intent.putExtra(TARGET_USER, targetUser.user)
            intent.putExtra(PREV_ACTIVITY, ACTIVITY)
            startActivity(intent)
        }
    }



    private fun fetchUserByPatternListener(pattern: String) {
        adapter.clear()
        presenter?.fetchByPrefix(pattern)
    }

    private fun fetchUsers() {
        adapter.clear()
        presenter?.fetchUsers()
    }

    companion object {
        val TARGET_USER = "TARGET_USER"
        val PREV_ACTIVITY = "PREV_ACTIVITY"
        val ACTIVITY = "USER_SEARCH_ACTIVITY"
    }

}


class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.nickname_raw_search).text =
            user.nickname.substring(0, user.nickname.indexOf('@', 0, false))
        viewHolder.itemView.findViewById<TextView>(R.id.job_raw_search).text = user.job
        Glide.with(viewHolder.itemView.findViewById<TextView>(R.id.user_search_raw_avatar))
            .load(user.photoUrl)
            .into(viewHolder.itemView.findViewById<ImageView>(R.id.user_search_raw_avatar))
    }

    override fun getLayout(): Int {
        return R.layout.user_search_row
    }
}

