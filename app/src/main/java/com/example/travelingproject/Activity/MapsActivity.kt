package com.example.travelingproject.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.travelingproject.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import com.example.travelingproject.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up the search view listener
        binding.mapSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform search action here
                query?.let { searchLocation(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You might want to implement search suggestions here
                return false
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a default marker at a location
        val defaultLocation = LatLng(25.276987, 55.296249)
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation))
    }

    private fun searchLocation(query: String) {
        // Use Geocoder to get the location coordinates from the search query
        val geocoder = Geocoder(this)
        val addresses: List<Address> = geocoder.getFromLocationName(query, 1)!!

        if (addresses.isNotEmpty()) {
            // Get the first address
            val address = addresses[0]
            val location = LatLng(address.latitude, address.longitude)

            mMap.clear() // Clear previous markers
            mMap.addMarker(MarkerOptions().position(location).title(query))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
        } else {
            // Handle case where no location is found for the given query
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
        }
    }
}
