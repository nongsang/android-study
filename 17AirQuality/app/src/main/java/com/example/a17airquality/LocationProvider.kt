package com.example.a17airquality

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

class LocationProvider(val context: Context) {
    // 위도, 경도, 고도와 같은 위치 관련 정보를 가진 클래스

    private var location: Location? = null
    private var locationManager: LocationManager? = null

    init {
        // 초기화할 때 위치를 가져온다.
        getLocation()
    }

    private fun getLocation(): Location? {
        try {
            // 위치서비스 기능을 가져온다.
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            var gpsLocation: Location? = null
            var networkLocation: Location? = null

            val isGPSEnabled: Boolean = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled: Boolean = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                // 1. GPS 기반 위치나 네트워크 기반 위치를 사용할 수 없으면 null 반환
                return null
            } else {
                // 2. 둘 중 하나라도 사용할 수 있으면 둘 중 더 정밀한 위치를 반환

                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                )

                val hasCourseLocationPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                )

                // 정밀한 위치, 대략적 위치 추적 둘 중 하나라도 허용이 안되었으면 null 반환
                if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED
                    || hasCourseLocationPermission != PackageManager.PERMISSION_GRANTED
                )
                    return null

                // 네트워크 기반 위치 추적을 사용할 수 있으면 네트워크 기반 위치추적 사용
                if (isNetworkEnabled) {
                    networkLocation = locationManager?.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER
                    )
                }

                // GPS 기반 위치 추적을 사용할 수 있으면 GPS 기반 위치추적 사용
                if (isGPSEnabled) {
                    gpsLocation = locationManager?.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER
                    )
                }

                // 두 가지 방법 모두 가능하면
                if (gpsLocation != null && networkLocation != null) {
                    // 더 정밀한 위치를 알려주는 위치를 사용
                    location = if (gpsLocation.accuracy > networkLocation.accuracy) {
                        gpsLocation
                    } else {
                        networkLocation
                    }
                } else {
                    // 두 가지 방법 중 하나만 있으면 둘 중 하나만 반환
                    if (gpsLocation != null) {
                        location = gpsLocation
                    } else if (networkLocation != null) {
                        location = networkLocation
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }

    fun getLocationLatitude(): Double {
        return location?.latitude ?: 0.0
    }

    fun getLocationLongitude() :Double {
        return location?.longitude ?: 0.0
    }
}