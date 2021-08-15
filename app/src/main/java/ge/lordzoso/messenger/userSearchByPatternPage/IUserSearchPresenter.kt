package ge.lordzoso.messenger.userSearchByPatternPage

interface IUserSearchPresenter {
    fun fetchUsers()
    fun fetchByPrefix(pattern: String)
}