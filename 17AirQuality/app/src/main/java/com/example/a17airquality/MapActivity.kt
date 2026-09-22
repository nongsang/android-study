package com.example.a17airquality

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a17airquality.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityMapBinding

    private var mMap: GoogleMap? = null
    var currentLat : Double = 0.0
    var currentLng : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLat = intent.getDoubleExtra("currentLat", 0.0)
        currentLng = intent.getDoubleExtra("currentLng", 0.0)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        binding.btnCheckHere.setOnClickListener {
            mMap?.let {
                val intent = Intent()
                intent.putExtra("latitude", it.cameraPosition.target.latitude)
                intent.putExtra("longitude", it.cameraPosition.target.longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    // 맵이 준비가 완료되면 호출할 이벤트
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.let {
            val currentLocation = LatLng(currentLat, currentLng)
            it.setMaxZoomPreference(20.0f)  // 지도 줌 최대값 설정
            it.setMinZoomPreference(12.0f)  // 지도 줌 최소값 설정
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16.0f))
        }

        //setMarker()

        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(this@MapActivity)

            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()

            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longitude), 16.0f))
            setMarker()
        }
    }

    /// 마커 설정
    private fun setMarker () {
        mMap?.let {
            it.clear()
            val markerOptions = MarkerOptions()
            markerOptions.position(it.cameraPosition.target)
            markerOptions.title("마커 위치")
            val marker = it.addMarker(markerOptions)

            it.setOnCameraIdleListener {
                marker?.setPosition(it.cameraPosition.target)
            }
        }
    }
}