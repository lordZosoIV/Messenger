package ge.lordzoso.messenger.homePageActivity

import android.annotation.SuppressLint
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import ge.lordzoso.messenger.R
import ge.lordzoso.messenger.model.Message
import ge.lordzoso.messenger.model.User
import ge.lordzoso.messenger.utils.Utils

class MBinder {
    fun bind(viewHolder: com.xwray.groupie.GroupieViewHolder, message: Message) {
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

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}