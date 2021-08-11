@file:Suppress("UNCHECKED_CAST")

package ge.lordzoso.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


import java.util.*

class UserInfoPageActivity : AppCompatActivity() {
    private var uid = ""
    private var nickname = ""
    private var job = ""
    private var photoUrl = ""
    var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_page)
        uid = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        initView(uid, ref)
        handleSignOut()
        handleUserInfoUpdate(uid, ref)
        setUpImageView()
//        findViewById<Button>(R.id.user_select_avatar).setOnClickListener{
//            getContent.launch("image/*")
//        }


    }

    private fun setUpImageView(){
        findViewById<ImageView>(R.id.user_select_avatar).setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 0) {
            val photoUri = data?.data!!
            selectedPhotoUri = photoUri
            findViewById<ImageView>(R.id.user_select_avatar).setImageURI(photoUri)
            uploadImageToFirebaseStorage()
        }
    }



    private fun handleUserInfoUpdate(uid:String, ref: DatabaseReference) {
        findViewById<Button>(R.id.updateInfo_button).setOnClickListener{
            job = findViewById<TextView>(R.id.user_job).text.toString()
            val user = User(uid, nickname + "@gmail.com", job, photoUrl)
            ref.setValue(user)
        }
    }

    private fun handleSignOut() {
        findViewById<Button>(R.id.sign_out_button).setOnClickListener{
            Log.d("asd", "out")
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun initView(uid:String, ref: DatabaseReference) {
        ref.get().addOnSuccessListener{
            val user = User(it.value as HashMap<String, String>)
            job = user.job
            nickname = user.nickname
            nickname = nickname.substring(0, nickname.indexOf('@',0, false))
            photoUrl = user.photoUrl
            findViewById<TextView>(R.id.person_name).text = nickname
            findViewById<TextView>(R.id.user_job).text = job
            Glide.with(this).load(photoUrl).into(findViewById(R.id.user_select_avatar));
            Log.d(job, nickname)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("1", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("2", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("2", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, nickname + "@gmail.com", job, profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("1", "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("2", "Failed to set value to database: ${it.message}")
            }
    }


}