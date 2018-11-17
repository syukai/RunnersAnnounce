package jp.syukai.runnersannounce

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION = 10
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        val info = JobInfo
//            .Builder(1, ComponentName(this, LocService::class.java))
//            .setPeriodic(1000*5, 500)
//            .build()
//        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
//        scheduler.schedule(info)

        Log.d("MainActivity", "startService")
        this.locServiceIntent = Intent(this.applicationContext, LocService::class.java)
        this.startService (this.locServiceIntent)
        Log.d("MainActivity", "started")


        fab.setOnClickListener { view ->
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        Snackbar.make(view, location?.latitude.toString() + "/" + location?.longitude.toString() , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
            locationActivity()
            }
        }

        checkPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    // 位置情報許可の確認
    private fun checkPermission() {
        // 既に許可している
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
            PackageManager.PERMISSION_GRANTED){

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    System.out.println(location.toString())
//                    var toast = Toast(this)
//                    toast.setText("location:" + location.toString())
//                    toast.show()

                    // Got last known location. In some rare situations this can be null.
                }
//            locationActivity();
        }
        // 拒否していた場合
        else{
            requestLocationPermission()
        }
    }

    // Intent でLocation
    private fun locationActivity() {
        startActivity(Intent(application, LocationActivity::class.java))
    }

    // 許可を求める
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION
            )

        } else {
            val toast = Toast.makeText(
                this,
                "許可されないとアプリが実行できません", Toast.LENGTH_SHORT
            )
            toast.show()

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSION
            )

        }
    }
}
