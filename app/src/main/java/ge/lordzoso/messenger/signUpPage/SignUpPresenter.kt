package ge.lordzoso.messenger.signUpPage

class SignUpPresenter(activity:SignUpActivity) : ISignUpPresenter {

    private var view: SignUpActivity = activity
    private var interactor: SignUpInteractor = SignUpInteractor(activity)

    init {
        view.initView()
    }

    override fun register(username: String, password: String, job: String) {
        interactor.registerUser(username, password, job)
    }

}