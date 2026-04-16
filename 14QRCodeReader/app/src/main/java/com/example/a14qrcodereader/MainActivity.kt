package com.example.a14qrcodereader

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.impl.utils.futures.Futures
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.a14qrcodereader.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 카메라 프로바이더를 멀티스레드로 기능을 가져오는 작업 등록
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    // 권한 요청 태그, 0 이상이기만 하면 된다.
    private val PERMISSIONS_REQUEST_CODE = 1
    // 권한 요청할 기능 리스트
    private val PERMISSIONS_REQUESTED = arrayOf(Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // 이 컨텍스트의 권한이 하나라도 없으면
        if (!hasPermissions(this)) {
            // 필요한 모든 권한 요청
            requestPermissions(PERMISSIONS_REQUESTED, PERMISSIONS_REQUEST_CODE)
        // 모든 권한을 얻었으면
        } else {
             // 카메라 실행
            startCamera()
        }
    }
    
    // 필요한 모든 권한 유무 확인
    fun hasPermissions(context: Context) = PERMISSIONS_REQUESTED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    // requestPermissions() 요청의 결과로 호출되는 함수
    // 오버라이딩 했다.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        // requestPermissions()의 인수로 넣은 PERMISSIONS_REQUEST_CODE와 맞는지 확인
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // 권한이 수락되면 카메라 시작
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(this, "권한 요청이 승인되었습니다.", Toast.LENGTH_SHORT).show()
                startCamera()
            // 권한이 거부되는 겨웅 액티비티 종료
            } else {
                Toast.makeText(this, "권한 요청이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 카메라를 작동하여 미리보기 및 이미지 분석
    fun startCamera() {
        // 스레드를 사용하는 구식 방식으로 카메라 프로바이더를 가져오는 방식
        {
            // 카메라 프로바이더의 인스턴스를 가져온다.
            // 카메라 초기화에 시간이 걸리므로 미래객체를(ListenableFuture)를 반환하고 비동기로 초기화한다.
            cameraProviderFuture = ProcessCameraProvider.getInstance(this)

            // 카메라의 초기화가 끝나면 addListener로 등록한 함수가 실행된다.
            cameraProviderFuture.addListener(Runnable {
                // 카메라 프로바이더 획득
                val cameraProvider = cameraProviderFuture.get()

                // 프리뷰 객체
                val preview = getPreview()
                // 후면 카메라 사용
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // 미리보기 기능 선택
                cameraProvider.bindToLifecycle(this, cameraSelector, preview)

            }, ContextCompat.getMainExecutor((this)))
        }

        // 안드로이드의 생명 주기에 맞춰 코루틴을 안전하게 관리하는 코루틴 인식범위 지정
        lifecycleScope.launch {
            try {
                // 코루틴을 사용하여 비동기로 동작하며, 조건에 맞으면 resume으로 코루틴을 종료한다.
                val cameraProvider =
                    suspendCancellableCoroutine<ProcessCameraProvider> { continuation ->
                        // 카메라 프로바이더의 인스턴스를 가져온다.
                        // 카메라 초기화에 시간이 걸리므로 미래객체를(ListenableFuture)를 반환하고 비동기로 초기화한다.
                        cameraProviderFuture = ProcessCameraProvider.getInstance(this@MainActivity)

                        // 카메라의 초기화가 끝나면 addListener로 등록한 함수가 실행된다.
                        // resume()으로 provider를 반환하면서 코루틴을 종료한다.
                        cameraProviderFuture.addListener({
                            try {
                                val provider = cameraProviderFuture.get()
                                continuation.resume(provider)
                            } catch (e: Exception) {
                                continuation.resumeWithException(e)
                            }
                        }, ContextCompat.getMainExecutor(this@MainActivity))

                        // 코루틴이 외부에서 취소되었을 때 호출
                        // 메모리 누수 차단
                        continuation.invokeOnCancellation {
                            cameraProviderFuture.cancel(true)
                        }
                    }

                // 프리뷰 객체
                val preview = getPreview()
                // 후면 카메라 사용
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                // 미리보기 기능 선택
                cameraProvider.bindToLifecycle(this@MainActivity, cameraSelector, preview)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 프리뷰 객체 반환
    fun getPreview(): Preview {
        // 프리뷰 객체 생성
        val preview = Preview.Builder().build()
        // 프리뷰 객체의 Surface 프로바이더를 등록
        // 서페이스는 픽셀들이 모여있는 객체다.
        // 프래그먼트 쉐이더의 프래그먼트와 비슷하다.
        preview.surfaceProvider = binding.barcodePreview.surfaceProvider

        return preview
    }
}