package jp.syukai.runnersannounce

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class LocService : Service() {

    override fun onCreate() {
        super.onCreate()
        System.out.println("service create")
    }

    override fun onBind(intent: Intent): IBinder {
        System.out.println("service running")
        Log.d("service running", "service running now")
        TODO("Return the communication channel to the service.")
    }


}
