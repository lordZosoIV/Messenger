package ge.lordzoso.messenger.chatPage

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shashank.sony.fancytoastlib.FancyToast
import ge.lordzoso.messenger.model.Message
import ge.lordzoso.messenger.model.User
import ge.lordzoso.messenger.utils.Utils

class ChatInteractor(activity: ChatActivity) {

    private var view: ChatActivity = activity

    fun listenMessages(user: User) {
        val from = FirebaseAuth.getInstance().uid
        val to = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$from/$to")
        val c = this

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)

                if (chatMessage != null) {
                    view.sleep()
                    if (chatMessage.from == from) {
                        view.chatRAdd(ChatR(chatMessage.text, Utils().getTimeAgo(chatMessage.time)))
                    } else {
                        view.chatLAdd(ChatL(chatMessage.text, Utils().getTimeAgo(chatMessage.time)))
                    }
                    view.scrollDown()
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
                FancyToast.makeText(view,"No Data", FancyToast.LENGTH_LONG, FancyToast.INFO,false).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists())
                    FancyToast.makeText(view,"No Messages Yet. Start Chat", FancyToast.LENGTH_LONG, FancyToast.INFO,false).show()
                return
            }
        })
    }

    fun sendMessage(msg: String, to: String?) {
        val from = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/messages/$from/$to").push()
        val refRev = FirebaseDatabase.getInstance().getReference("/messages/$to/$from").push()

        if (from == null || to == null) return

        val message = Message(ref.key!!, msg, from, to, System.currentTimeMillis() / 1000)

        ref.setValue(message).addOnSuccessListener {
            refRev.setValue(message).addOnSuccessListener {
                val reference = FirebaseDatabase.getInstance().getReference("/last-messages/$from/$to")
                val referenceRev = FirebaseDatabase.getInstance().getReference("/last-messages/$to/$from")
                reference.setValue(message).addOnSuccessListener {
                    referenceRev.setValue(message).addOnSuccessListener {
                        view.clear()
                    }

                }
            }
        }

    }
}