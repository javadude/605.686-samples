package com.javadude.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

// CHANGES FROM VIDEO
// - converted to kotlin
// - replaced my permissions manager with quickpermissions-kotlin
// - fixed broken location support (looks like Google finally killed it from GoogleMap)
// - disabled routing API call - as of December 2018 it requires an API key with a credit card on file
//     (note - navigation button still works)
// - added long-press on map to set a dummy location for setting the car location
//     = long press the map to drop a marker to explicitly set where your car is
//     = drop the car marker
//     = pressing navigation button will show route from real current location to car

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var map: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var blueCar: BitmapDescriptor? = null
    private var dummyLocationIcon: BitmapDescriptor? = null
    private var savedPosition: LatLng? = null
    private var marker: Marker? = null
    private var dummyLocationMarker: Marker? = null
//    private var route: Polyline? = null // DISABLED - AS OF DECEMBER 2018 REQUIRES AN API KEY WITH CREDIT CARD ACCOUNT

    @SuppressLint("MissingPermission")
    fun enableLocation() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
        map!!.isMyLocationEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        blueCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_blue)

        dummyLocationIcon = BitmapDescriptorFactory.fromBitmap(toBitmap(R.drawable.ic_my_location_white_24dp))
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val preferences = getSharedPreferences("position", Context.MODE_PRIVATE)
        val latitudeString = preferences.getString("latitude", null)
        val longitudeString = preferences.getString("longitude", null)

        if (latitudeString != null && longitudeString != null) {
            LatLng(latitudeString.toDouble(), longitudeString.toDouble()).let {
                savedPosition = it
                marker?.remove()
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(blueCar)
                        .title("Saved Location")
                        .snippet("Lat/Lng: " + it.latitude + ", " + it.longitude)
                        .position(it)
                )
            }
        }

        googleMap.setOnMapLongClickListener {
            dummyLocationMarker?.remove()
            dummyLocationMarker = googleMap.addMarker(
                    MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .icon(dummyLocationIcon)
                        .title("Dummy Location")
                        .snippet("Lat/Lng: " + it.latitude + ", " + it.longitude)
                        .position(it)
                )
        }
        enableLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        if (map?.isMyLocationEnabled == true)
            map?.isMyLocationEnabled = false
        savedPosition?.let {
            getSharedPreferences("position", Context.MODE_PRIVATE)
                .edit()
                .putString("latitude", it.latitude.toString() + "")
                .putString("longitude", it.longitude.toString() + "")
                .apply()
        }
    }

    override fun onResume() {
        super.onResume()
        mapFragment?.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_navigate -> {
                savedPosition?.let {
                    val uri = Uri.parse("google.navigation:q=" + it.latitude + "," + it.longitude)
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                return true
            }
// DISABLED - AS OF DECEMBER 2018 REQUIRES AN API KEY WITH CREDIT CARD ACCOUNT
//            R.id.action_show_route -> {
//                savedPosition?.let {
//                    val myLocation = map!!.myLocation
//                    RouteFetcher().execute(
//                        myLocation.latitude,
//                        myLocation.longitude,
//                        it.latitude,
//                        it.longitude
//                    )
//                }
//                return true
//            }
            R.id.action_remember_location -> {
                val myLocation = dummyLocationMarker?.position?.let {
                    Pair(it.latitude, it.longitude)
                }
                    ?: map?.myLocation?.let { Pair(it.latitude, it.longitude) }
                    ?: Pair(0.0, 0.0)

                marker?.remove()
                savedPosition = LatLng(myLocation.first, myLocation.second).apply {
                    marker = map!!.addMarker(
                        MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .icon(blueCar)
                            .title("Saved Location")
                            .position(this)
                    )
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

// DISABLED - AS OF DECEMBER 2018 REQUIRES AN API KEY WITH CREDIT CARD ACCOUNT
//    private inner class RouteFetcher : AsyncTask<Double, Void, List<LatLng>>() {
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            if (route != null)
//                route!!.remove()
//        }
//
//        override fun onPostExecute(latLngs: List<LatLng>) {
//            super.onPostExecute(latLngs)
//            route = map!!.addPolyline(
//                PolylineOptions()
//                    .addAll(latLngs)
//                    .color(Color.RED)
//                    .width(8f)
//            )
//        }
//
//        override fun doInBackground(vararg params: Double?): List<LatLng> {
//            val myLat = params[0]
//            val myLon = params[1]
//            val savedLat = params[2]
//            val savedLon = params[3]
//            try {
//                val uriString =
//                    "http://maps.googleapis.com/maps/api/directions/json?mode=walking&origin=$myLat,$myLon&destination=$savedLat,$savedLon&sensor=true"
//
//                val url = URL(uriString)
//                val connection = url.openConnection() as HttpURLConnection
//
//                connection.requestMethod = "GET"
//                connection.connect()
//                val responseCode = connection.responseCode
//                if (responseCode < 300) {
//                    BufferedReader(InputStreamReader(connection.inputStream)).use {
//                        val content = it.readText()
//                        Log.d("!!STUFF", content)
//                        // SHOW THE ROUTE ON THE MAP
//                        val jsonObject = JSONObject(content)
//                        val steps = jsonObject
//                            .getJSONArray("routes")
//                            .getJSONObject(0)
//                            .getJSONArray("legs")
//                            .getJSONObject(0)
//                            .getJSONArray("steps")
//                        val allLatLngs = ArrayList<LatLng>()
//                        for (i in 0 until steps.length()) {
//                            val step = steps.getJSONObject(i)
//                            val points = step.getJSONObject("polyline").getString("points")
//                            val latLngs = PolyUtil.decode(points)
//                            allLatLngs.addAll(latLngs)
//                        }
//                        return allLatLngs
//                    }
//                }
//
//                throw RuntimeException("Could not access routing information. Response from server: $responseCode")
//
//            } catch (e: IOException) {
//                throw RuntimeException(e)
//            } catch (e: JSONException) {
//                throw RuntimeException(e)
//            }
//        }
//    }

    // adapted from https://gist.github.com/Gnzlt/6ddc846ef68c587d559f1e1fcd0900d3
    private fun VectorDrawable.toBitmap() =
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }

    private fun VectorDrawableCompat.toBitmap() =
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }

    private fun toBitmap(drawableResId : Int) =
        when (val drawable = ContextCompat.getDrawable(this, drawableResId)) {
            is BitmapDrawable -> drawable.bitmap
            is VectorDrawableCompat -> drawable.toBitmap()
            is VectorDrawable -> drawable.toBitmap()
            else -> throw IllegalArgumentException("Unsupported drawable type")
        }
}
