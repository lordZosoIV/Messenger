package ge.lordzoso.messenger.mainPage

interface IMainActivityView {

    fun initView()
    fun goTo(nextClass: Class<*>)
    fun login()
}