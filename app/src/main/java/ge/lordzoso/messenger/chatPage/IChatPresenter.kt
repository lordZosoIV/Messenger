package ge.lordzoso.messenger.chatPage

import ge.lordzoso.messenger.model.User

interface IChatPresenter {
    fun listenMessages(user: User)
    fun sendMessage(msg: String, to: String?)
}