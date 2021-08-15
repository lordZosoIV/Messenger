package ge.lordzoso.messenger.userSearchByPatternPage

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shashank.sony.fancytoastlib.FancyToast
import ge.lordzoso.messenger.model.User
import ge.lordzoso.messenger.UserItem
import ge.lordzoso.messenger.UserSearchActivity

@Suppress("UNCHECKED_CAST")
class UserSearchInteractor(activity: UserSearchActivity) {

    private var view: UserSearchActivity = activity

    fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val c = view
        val me = FirebaseAuth.getInstance().uid
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            var exists = false
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = User(it.value as HashMap<String, String>)
                    if (user.uid != me) {
                        view.adapterAdd(UserItem(user))
                        exists = true
                    }
                    view.sleep()
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

                view.adapterSetListener()

                view.setAdapter()
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("error", "database error on fetching users for search")
            }
        })
    }

    fun fetchByPrefix(pattern: String) {
        val q = FirebaseDatabase.getInstance().getReference("/users")
        val c = view
        val me = FirebaseAuth.getInstance().uid
        q.addListenerForSingleValueEvent(object : ValueEventListener {
            var exists = false
            override fun onDataChange(datasnapshot: DataSnapshot) {
                datasnapshot.children.forEach {
                    val user = User(it.value as HashMap<String, String>)
                    if (user.uid != me && user.nickname.startsWith(pattern)) {
                        view.adapterAdd(UserItem(user))
                        exists = true
                        view.sleep()
                    }
                }

//                if(!exists){
//                    FancyToast.makeText(c,"No Such User", FancyToast.LENGTH_LONG, FancyToast.INFO,false).show()
//                }else{
//                    FancyToast.makeText(c,"No Such User", FancyToast.LENGTH_LONG, FancyToast.INFO,false).cancel()
//                }

                view.adapterSetListener()

                view.setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}