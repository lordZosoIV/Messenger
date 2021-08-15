package ge.lordzoso.messenger.userInfoPage

import android.net.Uri
import java.util.*

class UserInfoPresenter(activity: UserInfoPageActivity) : IUserInfoPresenter {

    private var view: UserInfoPageActivity = activity
    private var interactor: UserInfoInteractor = UserInfoInteractor(activity)


    init {
        view.initView()
        loadInfo()
    }

    override fun loadInfo() {
        interactor.loadInfoFromFirebase()
    }

    override fun logout() {
        interactor.logOut()
    }

    override fun updateUserInfo(job: String) {
        interactor.update(job)
    }

    override fun uploadImageToFBStorage(selectedPhotoUri: Uri) {
        val filename = UUID.randomUUID().toString()
        interactor.uploadImageToFirebaseStorage(selectedPhotoUri, filename)
    }

}