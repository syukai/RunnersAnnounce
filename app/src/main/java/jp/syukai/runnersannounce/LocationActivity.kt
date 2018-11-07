package jp.syukai.runnersannounce

import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.Button
import com.google.android.gms.location.*

import kotlinx.android.synthetic.main.activity_location.*
import java.time.LocalDateTime
import kotlinx.android.synthetic.main.content_location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.widget.Toast
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import android.os.Looper
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.support.v4.app.ActivityCompat
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.util.Log
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.OnSuccessListener
import android.Manifest


class LocationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var settingsClient: SettingsClient
//    private var priority: Int = 0
    private lateinit var locationCallback: LocationCallback
//    private var location: Location? = null
//    private var lastUpdatetime: LocalDateTime = LocalDateTime.now()
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private val REQUEST_CHECK_SETTINGS = 0x1
    private var requestingLocationUpdates:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        setSupportActionBar(toolbar)

        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        this.settingsClient = LocationServices.getSettingsClient(this)

        this.locationCallback = createLocationCallback()
        this.locationRequest = createLoccationRequest(LocationRequest.PRIORITY_HIGH_ACCURACY)
        this.locationSettingsRequest = buildLocationSettingsRequest(locationRequest)

        // 測位開始
        val buttonStart = findViewById<Button>(R.id.buttonStart)
        buttonStart.setOnClickListener { startLocationUpdates() }

        // 測位終了
        val buttonStop = findViewById<Button>(R.id.buttonStop)
        buttonStop.setOnClickListener { stopLocationUpdates() }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun createLocationCallback():LocationCallback{
        return object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                if(p0!=null){
                    updateLocationUI(p0.lastLocation, LocalDateTime.now())
                }
            }
        }
    }

    private fun updateLocationUI(location:Location, lastUpdateTime:LocalDateTime) {

        val fusedName = arrayOf("Latitude", "Longitude", "Accuracy", "Altitude", "Speed", "Bearing")

        val fusedData = doubleArrayOf(
            location.latitude,
            location.longitude,
            location.accuracy.toDouble(),
            location.altitude,
            location.speed.toDouble(),
            location.bearing.toDouble()
        )

        val strBuf = StringBuilder("---------- UpdateLocation ---------- \n")

        for (i in fusedName.indices) {
            strBuf.append(fusedName[i])
            strBuf.append(" = ")
            strBuf.append(fusedData[i].toString())
            strBuf.append("\n")
        }

        strBuf.append("Time")
        strBuf.append(" = ")
        strBuf.append(lastUpdateTime)
        strBuf.append("\n")

        textView.text = textView.text.toString() + strBuf.toString()
    }

    private fun createLoccationRequest(priority:Int):LocationRequest{
        locationRequest = LocationRequest()
        locationRequest.priority = priority
        locationRequest.interval = 60 * 1000
        locationRequest.fastestInterval = 5 * 1000
        return locationRequest

    }

    // 端末で測位できる状態か確認する。wifi, GPSなどがOffになっているとエラー情報のダイアログが出る
    private fun buildLocationSettingsRequest(locationRequest: LocationRequest):LocationSettingsRequest {
        val builder = LocationSettingsRequest.Builder()

        builder.addLocationRequest(locationRequest)
        return builder.build()
    }

    // FusedLocationApiによるlocation updatesをリクエスト
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener(this,
                OnSuccessListener {
                    Log.i("debug", "All location settings are satisfied.")

                    // パーミッションの確認
                    if (ActivityCompat.checkSelfPermission(
                            this@LocationActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this@LocationActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return@OnSuccessListener
                    }
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest, locationCallback, Looper.myLooper()
                    )
                })
            .addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            "debug",
                            "Location settings are not satisfied. Attempting to upgrade " + "location settings "
                        )
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            val rae = e as ResolvableApiException
                            rae.startResolutionForResult(
                                this@LocationActivity,
                                REQUEST_CHECK_SETTINGS
                            )

                        } catch (sie: IntentSender.SendIntentException) {
                            Log.i("debug", "PendingIntent unable to execute request.")
                        }

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage =
                            "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                        Log.e("debug", errorMessage)
                        Toast.makeText(
                            this@LocationActivity,
                            errorMessage, Toast.LENGTH_LONG
                        ).show()

                        requestingLocationUpdates = false
                    }
                }
            }

        requestingLocationUpdates = true
    }

    private fun stopLocationUpdates() {
        textView.text = textView.text.toString() + "onStop()¥n"

        if (!requestingLocationUpdates) {
            Log.d("debug", "stopLocationUpdates: " + "updates never requested, no-op.")


            return
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
            .addOnCompleteListener(
                this
            ) { requestingLocationUpdates = false }
    }

}
