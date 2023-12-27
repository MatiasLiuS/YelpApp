package com.example.project_1

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.project_1.databinding.ActivityMapsBinding
import com.google.android.gms.maps.MapsInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import android.content.SharedPreferences
import android.preference.PreferenceManager

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var zoomOutButton: Button
    private lateinit var zoomInButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var mapNewsAdapter: MapNewsAdapter
    private lateinit var locationText: TextView
    private lateinit var entireLayout: LinearLayout
    private lateinit var state: String
    private lateinit var sharedPreferences: SharedPreferences
    private var savedLatitude: Double = 0.0
    private var savedLongitude: Double = 0.0


    private var allMapNews: List<MapNewsBusiness> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        zoomOutButton = findViewById(R.id.zoomOutButton)
        zoomInButton = findViewById(R.id.zoomInButton)
        mapNewsAdapter = MapNewsAdapter(emptyList())
        locationText = findViewById(R.id.locationText)
        entireLayout = findViewById(R.id.entireLayout)
        entireLayout.visibility = View.GONE


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)


        savedLatitude = sharedPreferences.getFloat("saved_latitude", 0f).toDouble()
        savedLongitude = sharedPreferences.getFloat("saved_longitude", 0f).toDouble()


        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST){}


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        zoomOutButton.setOnClickListener {
            zoomOutMap()
        }
        zoomInButton.setOnClickListener {
            zoomInMap()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (savedLatitude != 0.0 && savedLongitude != 0.0) {
            val savedMarkerPosition = LatLng(savedLatitude, savedLongitude)
            mMap.addMarker(MarkerOptions().position(savedMarkerPosition).title("Saved Marker"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(savedMarkerPosition))
        }
        mMap.setOnMapLongClickListener { latLng ->
            Log.d("Maps", "long click at ${latLng.latitude}, ${latLng.longitude}")

            mMap.clear()
            val geocoder = Geocoder(this, Locale.getDefault())
            val results = try {
                geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
                )
            } catch (exception: Exception) {
                Log.e("Maps", "Geocoding failed", exception)
                listOf<Address>()
            }

            if (results.isNullOrEmpty()) {
                Log.e("Maps", "No addresses found")
            } else {
                val currentAddress = results[0]
                if (currentAddress != null && currentAddress.adminArea != null){
                state = currentAddress.adminArea
                locationText.text = ("Results for $state")
                state = state.replace(" ","-")
                    val editor = sharedPreferences.edit()
                    editor.putString("selected_state", state)

                    editor.putFloat("saved_latitude", latLng.latitude.toFloat())
                    editor.putFloat("saved_longitude", latLng.longitude.toFloat())

                    editor.apply()
                mMap.addMarker(MarkerOptions().position(latLng).title(state))
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5f)
                mMap.moveCamera(cameraUpdate)





                    recyclerView = findViewById(R.id.newsRecyclerView)
                val adapter = MapNewsAdapter(emptyList())
                val apiKey = getString(R.string.news_api_key)
                recyclerView.adapter = mapNewsAdapter
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                CoroutineScope(Dispatchers.IO).launch {
                    try{
                        allMapNews = MapNewsManager().retrieveMapNews(apiKey, state)
                        Log.d("MapsActivity", "Number of items retrieved from API: ${allMapNews.size}")
                        withContext(Dispatchers.Main) {
                            mapNewsAdapter.updateData(allMapNews)

                        }
                    } catch (e: Exception){
                        Log.e("MapsActivity", "Error fetching data: ${e.message}", e)

                        }
                    }
                }else {
                    Log.e("Maps", "No address found for the provided location")
                }
            }


            entireLayout.visibility = View.VISIBLE

        }
    }

    private fun zoomOutMap() {
        val currentZoom = mMap.cameraPosition.zoom
        val newZoom = currentZoom - 1
        val cameraUpdate = CameraUpdateFactory.zoomTo(newZoom)
        mMap.animateCamera(cameraUpdate)
    }
    private fun zoomInMap() {
        val currentZoom = mMap.cameraPosition.zoom
        val newZoom = currentZoom + 1
        val cameraUpdate = CameraUpdateFactory.zoomTo(newZoom)
        mMap.animateCamera(cameraUpdate)
    }


}
