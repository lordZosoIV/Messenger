package ge.lordzoso.messenger.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    private  val SECOND_MILLIS = 1000
    private  val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private  val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private  val DAY_MILLIS = 24 * HOUR_MILLIS


    @SuppressLint("SimpleDateFormat")
    fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeAgo(t: Long): String {
        var time = t
        if (time < 1000000000000L) {
            time *= 1000
        }

        val now = currentTimeToLong()
        if (time > now || time <= 0) {
            return "in the future"
        }

        val diff = now - time
        return when {
            diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} m"
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} h"
            else -> SimpleDateFormat("d.MMM").format(Date(t * 1000)).replace('.', ' ')
        }
    }

    fun currentTimeToLong(): Long {
        return System.currentTimeMillis()
    }


}