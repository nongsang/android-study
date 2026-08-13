package com.example.a16airquality

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a16airquality.databinding.ActivityMainBinding
import com.example.a16airquality.retrofit.AirQualityResponse
import com.example.a16airquality.retrofit.AirQualityService
import com.example.a16airquality.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // 런타임 권한 요청 시 필요한 요청 코드
    private val PERMISSION_REQUEST_CODE = 100

    // 권한 목록
    var REQUEST_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var locationProvider: LocationProvider

    lateinit var getGPSPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        checkAllPermission()
        updateUI()
        setRefreshButton()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setRefreshButton() {
        binding.btnRefresh.setOnClickListener {
            updateUI()
        }
    }

    // 권한 확인
    fun checkAllPermission() {
        if (!isLocationServiceAvailable()) {
            showDialogForLocationServiceSetting()
        } else {
            isRunTimePermissionsGranted()
        }
    }

    // 위치 서비스 사용중인지 확인
    fun isLocationServiceAvailable(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // GPS, 네트워크 서비스 사용중인지 확인
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    // 위치 추적할 권한을 허용했는지 확인
    fun isRunTimePermissionsGranted() {
        val hasFineLocationPermissions = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@MainActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // 둘 중 하나라도 권한 거부했으면 권한 얻기 요청
        if (hasFineLocationPermissions != PackageManager.PERMISSION_GRANTED
            || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE
            )
        }
    }

    // 권한 요청에 대한 결과는 이곳에서 처리한다.
    // requestCode: ActivityCompat.requestPermissions()에서 설정한, 모든 권한 요청이 완료됬을 때 반환할 번호
    // permissions: 요청한 기능 권한들
    // grantResults: 요청한 기능의 권한이 승인되었는지, 거부되었는지에 대한 상태 배열
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.size == REQUEST_PERMISSIONS.size) {

            var checkResult = true

            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    checkResult = false
                    break
                }
            }

            // 모든 권한이 승인되었으면
            if (checkResult) {
                // 위치값을 가져온다.
            } else {
                // 권한이 거부되었으면 앱 종료
                Toast.makeText(this@MainActivity, "권한이 거부되었습니다. 앱을 다시 실행하여 권한을 허용해주세요", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        // 미리 만든 런처에 GPS 권한 요청 기능 설정
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->

            // 설정 앱에서 돌아온 이후
            if (result.resultCode == Activity.RESULT_OK) {
                // 위치 서비스를 사용 설정이 되어있다면
                if (isLocationServiceAvailable()) {
                    // 런타인 권한을 요청했는지 검사
                    isRunTimePermissionsGranted()
                } else {
                    Toast.makeText(this@MainActivity, "위치 서비스를 사용할 수 없습니다.", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)

        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져 있습니다. 설정해야 앱을 사용할 수 있습니다.")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            // 설정 앱으로 이동하는 인텐트 생성
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            // 인텐트를 실행한 이후 결과를 런처로 전달하여 런처 실행
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })

        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
            Toast.makeText(this@MainActivity, "기기에서 위치서비스(GPS) 설정 후 사용해주세요.", Toast.LENGTH_SHORT)
                .show()
            finish()
        })

        builder.create().show()
    }

    private fun updateUI() {
        locationProvider = LocationProvider(this@MainActivity)

        val latitude: Double = locationProvider.getLocationLatitude()
        val longitude: Double = locationProvider.getLocationLongitude()

        if (latitude != 0.0 || longitude != 0.0) {

            // 1. 현재 위치를 가져오고 UI 업데이트
            val address = getCurrentAddress(latitude, longitude)

            address?.let {
                binding.tvLocationTitle.text = "${it.thoroughfare}"
                binding.tvLocationSubtitle.text = "${it.countryName} ${it.adminArea}"
            }

            // 2. 현재 위치를 기준으로 미세먼지 농도 가져오고 UI 업데이트
            getAirQualityData(latitude, longitude)


        } else {
            Toast.makeText(this@MainActivity, "위도, 경도 정보를 가져올 수 없습니다. 새로고침을 눌러주세요.", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): Address? {

        // 지오코더 생성
        // 위도, 경도를 지명으로, 지명을 위도, 경도로 변환해주는 객체
        val geocoder = Geocoder(this, Locale.getDefault())

        var addresses: List<Address?>? = null

//        // 안드로이드 티라미수(13, API 레벨 33) 이상인 경우
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            // 비동기 처리로 동작하는 getFromLocation()로 주소 찾기
//            geocoder.getFromLocation(latitude, longitude, 7,
//                object : Geocoder.GeocodeListener {
//                    override fun onGeocode(results: List<Address?>) {
//                        addresses = results
//                    }
//
//                    override fun onError(errorMessage: String?) {
//                        super.onError(errorMessage)
//
//                        Toast.makeText(this@MainActivity, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG)
//                            .show()
//                    }
//                })
//        } else {
//            // 안드로이드 티라미수(13, API 레벨 33) 미만인 경우
//            // 동기 처리로 동작하는 getFromLocation()로 주소 찾기
//            // 동기 처리기 때문에 네트워크가 갑자기 꺼지는 등의 예외처리를 해야 한다.
//            addresses = try {
//                geocoder.getFromLocation(latitude, longitude, 7)
//            } catch (ioException: IOException) {
//                Toast.makeText(this@MainActivity, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG)
//                    .show()
//                return null
//            } catch (illegalArgumentException: IllegalArgumentException) {
//                Toast.makeText(this@MainActivity, "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG)
//                    .show()
//                return null
//            }!!
//
//        }

        // getCurrentAddress()가 동기 함수이므로 동기함수인 geocoder.getFromLocation()를 사용한다.
        // 가능하다면 비동기 geocoder.getFromLocation()로 수정해서 사용한다.
        addresses = try {
            geocoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(this@MainActivity, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG)
                .show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this@MainActivity, "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG)
                .show()
            return null
        }!!

        // 에러는 아니지만 주소가 없다면
        if (addresses.isEmpty()) {
            Toast.makeText(this@MainActivity, "주소가 발견되지 않았습니다.", Toast.LENGTH_LONG)
                .show()
            return null
        }

        val address = addresses[0]
        return address
    }

    private fun getAirQualityData(latitude: Double, longitude: Double) {
        // 레트로핏 연결 객체 생성
        // 사용할 메서드가 정의된 클래스를 설정해서 생성해야 한다.
        val retrofitAPI = RetrofitConnection.getInstance().create(AirQualityService::class.java)

        // 비동기적으로 서버에 응답을 보냄
        retrofitAPI.getAirQualityData(
            latitude.toString(),
            longitude.toString(),
            "79ce1758-0fe0-406d-b6de-926d68cf088c"
        ).enqueue(object: Callback<AirQualityResponse> {
            // enqueue()는 비동기적으로 백그라운드 스레드에서 요청을 보내고 응답이 오면 콜백 함수를 실행한다.
            // 동기적으로 하려면 excute()를 사용하면 된다.

            // 서버에서 정보 전달 성공
            override fun onResponse(
                call: Call<AirQualityResponse?>,
                response: Response<AirQualityResponse?>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "최신 정보 업데이트 완료!", Toast.LENGTH_SHORT).show()

                    response.body()?.let {updateAirUI(it)}
                } else {
                    Toast.makeText(this@MainActivity, "업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // 서버에서 정보 전달 실패
            override fun onFailure(
                call: Call<AirQualityResponse?>,
                t: Throwable
            ) {
                t.printStackTrace()

                Toast.makeText(this@MainActivity, "업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateAirUI(airQualityResponse: AirQualityResponse) {
        val pollutionData = airQualityResponse.data.current.pollution

        // 오염도 수치
        binding.tvCount.text = pollutionData.aqius.toString()

        // 측정된 날짜
        val dateTime = ZonedDateTime.parse(pollutionData.ts)
            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))   // 서울 타임존으로 시간 변환
            .toLocalDateTime()  // 타임존과 시차 부분을 떼버린다

        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        binding.tvCheckTime.text = dateTime.format(dateTimeFormatter).toString()

        when(pollutionData.aqius) {
            in 0..50 -> {
                binding.tvTitle.text = "좋음"
                binding.imgBg.setImageResource(R.drawable.bg_good)
            }

            in 51..100 -> {
                binding.tvTitle.text = "보통"
                binding.imgBg.setImageResource(R.drawable.bg_soso)
            }

            in 101..150 -> {
                binding.tvTitle.text = "나쁨"
                binding.imgBg.setImageResource(R.drawable.bg_bad)
            }

            in 151..200 -> {
                binding.tvTitle.text = "매우 나쁨"
                binding.imgBg.setImageResource(R.drawable.bg_worst)
            }
        }
    }
}