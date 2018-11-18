package jp.syukai.runnersannounce

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class LocService : Service() {

    override fun onCreate() {
        super.onCreate()
        System.out.println("service create")
        Log.d("LocService", "service create")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        System.out.println("service Start Command")
        Log.d("LocService", "service Start Command")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        System.out.println("service running")
        Log.d("LocService", "service running now")
        TODO("Return the communication channel to the service.")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        System.out.println("service UnBind")
        Log.d("LocService", "service UnBind")
        return super.onUnbind(intent)
    }



}
