package ge.lordzoso.messenger

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.shashank.sony.fancytoastlib.FancyToast


class Loader {

    fun goLive(view: View){
        view.visibility = View.VISIBLE
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            sleep(view)
        }, 3000)

    }


    fun sleep(view: View){
        view.visibility = View.INVISIBLE
    }


}