package ge.lordzoso.messenger.userSearchByPatternPage

import ge.lordzoso.messenger.UserSearchActivity

class UserSearchPresenter(activity: UserSearchActivity) : IUserSearchPresenter {

    private var view: UserSearchActivity = activity
    private var interactor: UserSearchInteractor = UserSearchInteractor(activity)

    init {
        view.initView()
    }

    override fun fetchUsers() {
        interactor.fetchUsers()
    }

    override fun fetchByPrefix(pattern: String) {
       interactor.fetchByPrefix(pattern)
    }



}