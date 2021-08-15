package ge.lordzoso.messenger.model

class Message(val id: String, val text: String, val from: String, val to: String, val time: Long) {
    constructor() : this("", "", "", "", -1)
}