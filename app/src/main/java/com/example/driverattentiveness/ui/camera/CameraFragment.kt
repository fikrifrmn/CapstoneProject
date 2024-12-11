package com.example.driverattentiveness.ui.camera

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaPlayer
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.driverattentiveness.R
import com.example.driverattentiveness.databinding.FragmentCameraBinding
import io.socket.engineio.parser.Base64
import java.io.ByteArrayOutputStream
import java.net.URISyntaxException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), Detector.DetectorListener {

    companion object {
        private const val TAG = "Camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA
        )
    }

    private lateinit var binding: FragmentCameraBinding
    private lateinit var detector: Detector
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: CameraViewModel by viewModels()

    private lateinit var mSocket: Socket

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeSocket()

        // Initialize the detector
        detector = Detector(requireContext(), Constants.MODEL_PATH, Constants.LABELS_PATH, this)
        detector.setup()

        // Observe ViewModel LiveData
        observeViewModel()

        // Check permissions and start camera
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun initializeSocket() {
        Log.d(TAG, "Log Initialized")
        try {
            val opts = IO.Options()
            opts.path = "/wsio/socket.io"
            mSocket = IO.socket("http://34.101.170.152:8080", opts)
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket connected")
            }
            mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                // args is an array of Any, where the first element is usually the error
                val error = args[0] as? Throwable // Cast the first argument to Throwable
                Log.e(TAG, "Socket connection error: ${error?.message}", error)
            }
            mSocket.on("prediction_result") { args ->
                requireActivity().runOnUiThread {
                    if (args.isNotEmpty()) {
                        val prediction = args[0] as? Int // Interpret parameter sebagai Integer
                        Log.d(TAG, "Prediction result: $prediction")
                        when (prediction) {
                            1 -> viewModel.updatePredictionResult("You are driving safely")
                            0 -> viewModel.updatePredictionResult("You are not driving safely")
                            -1 -> viewModel.updatePredictionResult("No detection")
                            else -> Log.e(TAG, "Unexpected prediction value: $prediction")
                        }
                    } else {
                        Log.e(TAG, "Received empty prediction result")
                    }
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val rotation = binding.viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        val preview = Preview.Builder()
            .setTargetResolution(Size(640, 640))
            .setTargetRotation(rotation)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 640)) // Set resolusi eksplisit
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
            try {
                val bitmap = processImage(imageProxy)
                detector.detect(bitmap)
                sendFrame(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Image processing failed", e)
            } finally {
                imageProxy.close()
            }
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
    }

    private fun processImage(imageProxy: ImageProxy): Bitmap {
        val buffer = imageProxy.planes[0].buffer
        val bitmap = Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        val matrix = Matrix().apply { postRotate(imageProxy.imageInfo.rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream) // You can change the format and quality
        val byteArray = byteArrayOutputStream.toByteArray()
        val base64Data = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return "data:image/jpeg;base64,$base64Data"
    }


    private fun sendFrame(rotatedBitmap: Bitmap) {
        // Convert Bitmap to Base64-encoded string
        val stream = ByteArrayOutputStream()
        val byteArray = stream.toByteArray()
        val base64String = bitmapToBase64(rotatedBitmap)
        mSocket.emit("image", base64String)

        if (mSocket.connected()) {
            mSocket.emit("image", base64String) // Send Base64 string
            Log.d(TAG, "Sending frame to server as Base64 string")
        } else {
            Log.e(TAG, "Socket not connected, cannot send frame")
        }
    }



    private fun observeViewModel() {
        viewModel.detectionText.observe(viewLifecycleOwner) { text ->
            binding.detectionTextView.text = text
            val color = if (text == "You are not driving safely") {
                ContextCompat.getColor(requireContext(), R.color.unsafe_driving)
            } else {
                ContextCompat.getColor(requireContext(), R.color.safe_driving)
            }
            binding.detectionTextView.setTextColor(color)
        }

        viewModel.inferenceTimeText.observe(viewLifecycleOwner) {
            binding.inferenceTime.text = it
        }

        viewModel.boundingBoxes.observe(viewLifecycleOwner) {
            binding.overlay.setResults(it)
            binding.overlay.invalidate()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        cameraExecutor.shutdown()
        mSocket.disconnect()
        Log.d(TAG, "onPause: Socket disconnected")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        mSocket.disconnect()
        mSocket.off("prediction_result")
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long) {
        viewModel.onDetect(boundingBoxes, inferenceTime)
    }

    override fun onEmptyDetect() {
        viewModel.onEmptyDetect()
    }
}
