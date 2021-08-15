package ge.lordzoso.messenger.userSearchByPatternPage

import ge.lordzoso.messenger.UserItem

interface IUserSearchByPatternView {
    fun goTo(nextClass: Class<*>)
    fun goLive()
    fun sleep()
    fun initView()
    fun adapterAdd(userItem: UserItem)
    fun setAdapter()
    fun adapterSetListener()
}