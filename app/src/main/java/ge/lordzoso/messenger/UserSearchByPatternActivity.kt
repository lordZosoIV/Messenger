package ge.lordzoso.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shashank.sony.fancytoastlib.FancyToast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.*
import kotlin.collections.HashMap


@Suppress("UNCHECKED_CAST")
class UserSearchActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    var me = ""

    @SuppressLint("ClickableViewAccessibility")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search_by_pattern)

        me = FirebaseAuth.getInstance().uid.toString()
        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))

        fetchUsers()

        findViewById<Button>(R.id.back).setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
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
                if (pattern.length > 3) {
                    fetchUserByPattern(pattern)
                }
            }
        })


    }

    private fun fetchUserByPattern(pattern: String) {
        adapter.clear()
        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))
        val q = FirebaseDatabase.getInstance().getReference("/users")
        val c = this

        q.addListenerForSingleValueEvent(object : ValueEventListener {
            var exists = false
            override fun onDataChange(datasnapshot: DataSnapshot) {
                datasnapshot.children.forEach {
                    val user = User(it.value as HashMap<String, String>)
                    if (user.uid != me && user.nickname.startsWith(pattern)) {
                        adapter.add(UserItem(user))
                        exists = true
                        Loader().sleep(findViewById<ProgressBar>(R.id.progressBar))
                    }
                }

//                if(!exists){
//                    FancyToast.makeText(c,"No Such User", FancyToast.LENGTH_LONG, FancyToast.INFO,false).show()
//                }else{
//                    FancyToast.makeText(c,"No Such User", FancyToast.LENGTH_LONG, FancyToast.INFO,false).cancel()
//                }

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatActivity::class.java)
                    val targetUser = item as UserItem
                    intent.putExtra(TARGET_USER, targetUser.user)
                    intent.putExtra(PREV_ACTIVITY, ACTIVITY)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun fetchUsers() {
        adapter.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val c = this
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            var exists = false
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = User(it.value as HashMap<String, String>)
                    if (user.uid != me) {
                        adapter.add(UserItem(user))
                        exists = true
                    }
                    Loader().sleep(findViewById<ProgressBar>(R.id.progressBar))
                }

                if (!exists) {
                    FancyToast.makeText(c,
                        "No Users",
                        FancyToast.LENGTH_LONG,
                        FancyToast.INFO,
                        false).show()
                } else {
                    FancyToast.makeText(c,
                        "No Users",
                        FancyToast.LENGTH_LONG,
                        FancyToast.INFO,
                        false).cancel()
                }

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatActivity::class.java)
                    val targetUser = item as UserItem
                    intent.putExtra(TARGET_USER, targetUser.user)
                    intent.putExtra(PREV_ACTIVITY, ACTIVITY)
                    startActivity(intent)
                }

                findViewById<RecyclerView>(R.id.searched_users).adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("error", "database error on fetching users for search")
            }
        })
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

