package ge.lordzoso.messenger.userInfoPage

import android.net.Uri

interface IUserInfoPresenter {

    fun loadInfo()
    fun logout()
    fun updateUserInfo(job: String)
    fun uploadImageToFBStorage(selectedPhotoUri: Uri)
}