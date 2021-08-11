package ge.lordzoso.messenger

class User(val uid:String, var nickname:String, val job:String, val photoUrl:String){
    constructor(hashMap: HashMap<String, String>) : this(hashMap["uid"]!!, hashMap["nickname"]!!, hashMap["job"]!!, hashMap["photoUrl"]!!)

}