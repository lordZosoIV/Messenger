package ge.lordzoso.messenger

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid:String, var nickname:String, val job:String, val photoUrl:String) : Parcelable{
    constructor(hashMap: HashMap<String, String>) : this(hashMap["uid"]!!, hashMap["nickname"]!!, hashMap["job"]!!, hashMap["photoUrl"]!!)
    constructor() : this("", "", "", "")

}