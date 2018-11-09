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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        System.out.println("service Start Command")
        Log.d("service running", "service Start Command")
    }

    override fun onBind(intent: Intent): IBinder {
        System.out.println("service running")
        Log.d("service running", "service running now")
        TODO("Return the communication channel to the service.")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
        System.out.println("service UnBind")
        Log.d("service running", "service UnBind")
    }


}
