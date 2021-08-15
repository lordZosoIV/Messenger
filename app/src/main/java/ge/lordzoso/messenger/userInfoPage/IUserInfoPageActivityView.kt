package ge.lordzoso.messenger.userInfoPage

interface IUserInfoPageActivityView {

    fun initView()
    fun setInfo(nickname: String, job: String, photoUrl: String)

    fun goTo(nextClass: Class<*>)
    fun goLive()
    fun sleep()
}