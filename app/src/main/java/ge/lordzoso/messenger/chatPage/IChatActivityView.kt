package ge.lordzoso.messenger.chatPage

interface IChatActivityView {
    fun goTo(nextClass: Class<*>)
    fun goLive()
    fun sleep()
    fun initView()
    fun chatRAdd(chatR: ChatR)
    fun chatLAdd(chatL: ChatL)
    fun scrollDown()
    fun listenForMessages()
    fun clear()
}