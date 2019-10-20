package com.javadude.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var blueCar: BitmapDescriptor
    private lateinit var myLocationIcon: BitmapDescriptor
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 4000
        fastestInterval = 2000
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            currentLocation = result?.locations?.last()?.toLatLng()
        }
    }

    private fun Location.toLatLng() = LatLng(latitude, longitude)

    private var currentLocation: LatLng? = null
        set(value) {
            field = value
            map?.let {
                myLocationMarker?.remove()
                value?.let { newLocation ->
                    myLocationMarker = it.addMarker(
                        MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .icon(myLocationIcon)
                            .position(newLocation)
                    )
                }
            }
        }

    private var map: GoogleMap? = null
    private var savedPosition: LatLng? = null
    private var marker: Marker? = null
    private var myLocationMarker: Marker? = null

    private var latLngBoundsPadding = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        latLngBoundsPadding = resources.getDimension(R.dimen.lat_lng_bounds_padding).toInt()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        blueCar = BitmapDescriptorFactory.fromResource(R.mipmap.car_blue) // already a bitmap drawable

        myLocationIcon = loadBitmapDescriptor(R.drawable.my_location)
    }

    private fun startLocationUpdates() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }
    private fun stopLocationUpdates() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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


        var latLngBounds : LatLngBounds? = null

        fun LatLng.addToBounds() {
            latLngBounds = latLngBounds?.including(this) ?: LatLngBounds(this, this)
        }

        // start the bounds around APL
        LatLng(39.163742, -76.900235).addToBounds()
        LatLng(39.163467, -76.897381).addToBounds()
        LatLng(39.162610, -76.899500).addToBounds()

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

        savedPosition?.addToBounds()

        googleMap.setOnMapLoadedCallback {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, latLngBoundsPadding))
        }

        googleMap.setOnMapLongClickListener {
            stopLocationUpdates()
            myLocationMarker?.remove()
            currentLocation = it
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
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
        startLocationUpdates()
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_navigate -> {
                currentLocation?.let { curr ->
                    savedPosition?.let { car ->
                        // see https://developers.google.com/maps/documentation/urls/guide
                        //   for details on specifying the URI - it's consistent across all platforms
                        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=${curr.latitude},${curr.longitude}&destination=${car.latitude},${car.longitude}&travelmode=walking")
                        startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
                            setPackage("com.google.android.apps.maps")
                        })
                    }
                }
                return true
            }

            R.id.action_remember_location -> {
                marker?.remove()
                savedPosition = currentLocation?.apply {
                    marker = map!!.addMarker(
                        MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .icon(blueCar)
                            .title("Parked Here!")
                            .position(this)
                    )
                }
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}

