@file:Suppress("UNCHECKED_CAST")

package ge.lordzoso.messenger.userInfoPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ge.lordzoso.messenger.*
import ge.lordzoso.messenger.homePageActivity.HomePageActivity
import ge.lordzoso.messenger.model.Loader

class UserInfoPageActivity : AppCompatActivity(), IUserInfoPageActivityView {

    private var presenter: IUserInfoPresenter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = UserInfoPresenter(this)

    }

    override fun initView() {
        setContentView(R.layout.activity_user_info_page)
        Loader().goLive(findViewById<ProgressBar>(R.id.progressBar))
        handleSignOut()
        handleUserInfoUpdate()
        setUpImageView()
        bottomBarHandle()
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

    override fun setInfo(nickname: String, job: String, photoUrl: String) {
        findViewById<TextView>(R.id.person_name).text = nickname
        findViewById<TextView>(R.id.user_job).text = job
        Glide.with(this).load(photoUrl).into(findViewById(R.id.user_select_avatar));
    }

    companion object {
        val PREV_ACTIVITY = "PREV_ACTIVITY"
        val ACTIVITY = "USER_INFO_PAGE_ACTIVITY"
    }

    private fun bottomBarHandle() {
        findViewById<FloatingActionButton>(R.id.fab_button).setOnClickListener {
            intent.putExtra(PREV_ACTIVITY, ACTIVITY)
            goTo(UserSearchActivity::class.java)
        }


        findViewById<BottomAppBar>(R.id.bottomAppbar).setNavigationOnClickListener {
            goTo(HomePageActivity::class.java)

        }
    }


    private fun setUpImageView() {
        findViewById<ImageView>(R.id.user_select_avatar).setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 0) {
            val photoUri = data?.data!!
            findViewById<ImageView>(R.id.user_select_avatar).setImageURI(photoUri)
            presenter?.uploadImageToFBStorage(photoUri)
        }
    }


    private fun handleUserInfoUpdate() {
        findViewById<Button>(R.id.updateInfo_button).setOnClickListener {
            presenter?.updateUserInfo(findViewById<TextView>(R.id.user_job).text.toString())
        }
    }

    private fun handleSignOut() {
        findViewById<Button>(R.id.sign_out_button).setOnClickListener {
            presenter?.logout()
        }
    }


}