package com.example.facebooklocation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facebooklocation.utils.GPSTracker
import kotlinx.android.synthetic.main.activity_maps.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.net.URL


class MapsActivity : AppCompatActivity() {

    lateinit var map: MapView
    lateinit var mapController: IMapController

    var name: String? = null
    var email: String? = null
    var profilePic: String? = null
    private var bitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        var ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        initView()

        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")
        profilePic = intent.getStringExtra("profilePic")

        //change image url to bitmap
        var thread = Thread {
            try {
                try {
                    val url = URL(profilePic)
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } catch (e: IOException) {
                    println(e)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

        showMap()
    }

    private fun initView() {
        image.setOnClickListener {
            finish()
        }
    }


    private fun showMap() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            initialiseMaps()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                initialiseMaps()
            }
        }
    }


    private fun initialiseMaps() {


        //change bitmap to drawable for marker icon but marker isnot able to draw the picture so i use default icon from drawable

        var picture = BitmapDrawable(resources, bitmap)



        map = findViewById(R.id.mapview)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        mapController = map.controller
        mapController.setZoom(15)

        val gps = GPSTracker(this)
        val latitude = gps.getLatitude()
        val longitude = gps.getLongitude()

        var startPoint = GeoPoint(latitude, longitude)


        mapController.setCenter(startPoint)


        var startMarker = Marker(map)

        startMarker.title =
            "Name:" + " " + name + "\n" + "Email:" + " " + email + "\n" + "Latitude: ${latitude}" + " Longitude: ${longitude}"
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.setIcon(resources.getDrawable(R.drawable.person_icon))
        map.overlays.add(startMarker)
        startMarker.position = startPoint
        startMarker.showInfoWindow()

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}

