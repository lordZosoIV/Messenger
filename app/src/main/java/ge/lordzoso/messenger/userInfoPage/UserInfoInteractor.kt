package ge.lordzoso.messenger.userInfoPage

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import ge.lordzoso.messenger.model.User
import ge.lordzoso.messenger.mainPage.MainActivity
import kotlin.collections.HashMap

@Suppress("UNCHECKED_CAST")
class UserInfoInteractor(activity: UserInfoPageActivity) {

    private var view: UserInfoPageActivity = activity
    private var uid = ""
    private var nickname = ""
    private var job = ""
    private var photoUrl = ""
    var selectedPhotoUri: Uri? = null


    fun loadInfoFromFirebase(){
        view.goLive()
        uid = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.get().addOnSuccessListener{
            val user = User(it.value as HashMap<String, String>)
            job = user.job
            nickname = user.nickname
            nickname = nickname.substring(0, nickname.indexOf('@',0, false))
            photoUrl = user.photoUrl
            view.setInfo(nickname, job, photoUrl)
            view.sleep()
        }
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        view.goTo(MainActivity::class.java)
    }

    fun update(newJob:String) {
        view.goLive()
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        job = newJob
        val user = User(uid, nickname + "@gmail.com", newJob, photoUrl)
        ref.setValue(user).addOnSuccessListener {
           view.sleep()
            FancyToast.makeText(view.applicationContext,"updated successfully",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show()
        }
    }

    fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri, filename: String) {
        view.goLive()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                view.sleep()
                FancyToast.makeText(view.applicationContext,"error in uploading",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, nickname + "@gmail.com", job, profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                view.sleep()
                FancyToast.makeText(view.applicationContext,"saved successfully",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show()
            }
            .addOnFailureListener {
                view.sleep()
                FancyToast.makeText(view.applicationContext,"error in saving user",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show()
            }
    }


}