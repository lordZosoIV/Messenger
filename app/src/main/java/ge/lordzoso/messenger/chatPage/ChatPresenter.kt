package ge.lordzoso.messenger.chatPage

import ge.lordzoso.messenger.model.User

class ChatPresenter(activity: ChatActivity) : IChatPresenter {
    private var view: ChatActivity = activity
    private var interactor: ChatInteractor = ChatInteractor(activity)

    init {
        view.initView()
    }

    override fun listenMessages(user: User) {
       interactor.listenMessages(user)
    }

    override fun sendMessage(msg: String, to: String?) {
        interactor.sendMessage(msg, to)
    }


}