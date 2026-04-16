package com.example.a14qrcodereader

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// QR 코드를 분석하는 클래스는 QR 코드를 분석한 이후 값을 실행할 인터페이스를 받아서 생성된다.
class QRCodeAnalyzer(val onDetectListener: OnDetectListener): ImageAnalysis.Analyzer {

    // 바코드 스캐닝 객체 생성
    // ML 키트 라이브러리를 포함해야 한다.
    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (mediaImage != null)
        {
            // 이미지가 찍힐 당시 카메라의 회전 각도를 고려하여 입력 이미지를 생성
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { qrCodes ->
                    // 화면에 찍힌 QR 코드의 데이터를 문자열로 전부 분석해서 배열로 생성한다.
                    for (qrCode in qrCodes) {
                        onDetectListener.onDetect(qrCode.rawValue ?: "")
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}