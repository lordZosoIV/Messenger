package ge.lordzoso.messenger.signUpPage

interface ISignUpActivityView {

    fun initView()
    fun register()
    fun goTo(nextClass: Class<*>)
}