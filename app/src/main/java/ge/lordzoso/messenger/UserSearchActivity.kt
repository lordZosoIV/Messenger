package ge.lordzoso.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.HashMap

@Suppress("UNCHECKED_CAST")
class UserSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)
        Log.d("Asd", "yba")
        fetchUsers()

    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = User(it.value as HashMap<String, String>)
                    adapter.add(UserItem(user))
                }

                findViewById<RecyclerView>(R.id.searched_users).adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("error", "database error on fetching users for search")
            }
        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.nickname_raw_search).text = user.nickname.substring(0, user.nickname.indexOf('@',0, false))
        viewHolder.itemView.findViewById<TextView>(R.id.job_raw_search).text = user.job
        Glide.with(viewHolder.itemView.findViewById<TextView>(R.id.user_search_raw_avatar)).load(user.photoUrl).
        into(viewHolder.itemView.findViewById<ImageView>(R.id.user_search_raw_avatar))
    }

    override fun getLayout(): Int {
        return R.layout.user_search_raw
    }
}

