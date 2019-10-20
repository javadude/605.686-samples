package com.javadude.maps.samples
import androidx.appcompat.app.AppCompatActivity
import import android.os.Bundle
import com.javadude.maps.R

class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val bitmap = loadBitmap(R.drawable.ic_my_location_white_24dp)

        // do something with the bitmap, like use it for an icon in Google Maps
        val bitmapDescriptor = bitmap.toBitmapDescriptor()
        // ... later set up markers using the descriptor
    }
}