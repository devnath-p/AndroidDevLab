package com.example.exp10map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var isReceivingUpdates = false

    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var btnGetLocation: MaterialButton
    private lateinit var btnToggleUpdates: MaterialButton
    private lateinit var mapWebView: WebView
    private lateinit var tvMapPlaceholder: TextView

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            tvStatus.setText(R.string.status_permission_granted)
            fetchCurrentLocation()
        } else {
            tvStatus.setText(R.string.status_permission_denied)
            Toast.makeText(this, R.string.status_permission_denied_toast, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        setupWebView()
        setupListeners()

        savedInstanceState?.let { bundle ->
            if (bundle.containsKey(KEY_LAT) && bundle.containsKey(KEY_LNG)) {
                val lat = bundle.getDouble(KEY_LAT)
                val lng = bundle.getDouble(KEY_LNG)
                lastLatitude = lat
                lastLongitude = lng
                loadMapInWebView(lat, lng)
            }
        }
    }

    private fun initViews() {
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvAccuracy = findViewById(R.id.tvAccuracy)
        tvStatus = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnToggleUpdates = findViewById(R.id.btnToggleUpdates)
        mapWebView = findViewById(R.id.mapWebView)
        tvMapPlaceholder = findViewById(R.id.tvMapPlaceholder)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        try {
            mapWebView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            mapWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    tvMapPlaceholder.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupListeners() {
        btnGetLocation.setOnClickListener {
            if (hasLocationPermission()) {
                fetchCurrentLocation()
            } else {
                requestLocationPermission()
            }
        }

        btnToggleUpdates.setOnClickListener {
            if (isReceivingUpdates) {
                stopLocationUpdates()
            } else {
                if (hasLocationPermission()) {
                    startLocationUpdates()
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        showLoading(isLoading = true)
        tvStatus.setText(R.string.status_acquiring_location)

        try {
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token,
            ).addOnSuccessListener { location: Location? ->
                showLoading(isLoading = false)
                if (location != null) {
                    updateLocationUI(location)
                } else {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                            if (lastLoc != null) {
                                updateLocationUI(lastLoc)
                            } else {
                                tvStatus.setText(R.string.status_location_null)
                            }
                        }.addOnFailureListener {
                            tvStatus.text = getString(R.string.status_fetch_failed, it.message ?: "")
                        }
                    } catch (e: Exception) {
                        tvStatus.text = getString(R.string.status_fetch_failed, e.message ?: "")
                    }
                }
            }.addOnFailureListener { exception ->
                showLoading(isLoading = false)
                tvStatus.text = getString(R.string.status_fetch_failed, exception.message ?: "")
            }
        } catch (e: Exception) {
            showLoading(isLoading = false)
            tvStatus.text = getString(R.string.status_fetch_failed, e.message ?: "")
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            requestLocationPermission()
            return
        }

        try {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L,
            ).apply {
                setMinUpdateIntervalMillis(2000L)
                setGranularity(Granularity.GRANULARITY_FINE)
                setWaitForAccurateLocation(true)
            }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        updateLocationUI(location)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper(),
            )

            isReceivingUpdates = true
            btnToggleUpdates.setText(R.string.btn_stop_updates)
            btnToggleUpdates.setIconResource(android.R.drawable.ic_media_pause)
            tvStatus.setText(R.string.status_receiving_updates)
        } catch (e: Exception) {
            tvStatus.text = getString(R.string.status_fetch_failed, e.message ?: "")
        }
    }

    private fun stopLocationUpdates() {
        try {
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isReceivingUpdates = false
        btnToggleUpdates.setText(R.string.btn_live_updates)
        btnToggleUpdates.setIconResource(android.R.drawable.ic_menu_rotate)
        tvStatus.setText(R.string.status_updates_stopped)
    }

    private fun updateLocationUI(location: Location) {
        val latStr = String.format(Locale.US, "%.6f", location.latitude)
        val lngStr = String.format(Locale.US, "%.6f", location.longitude)
        val accStr = String.format(Locale.US, "± %.1f m", location.accuracy)
        val timeStr = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(location.time))

        lastLatitude = location.latitude
        lastLongitude = location.longitude

        tvLatitude.text = latStr
        tvLongitude.text = lngStr
        tvAccuracy.text = accStr
        tvStatus.text = getString(R.string.status_updated_at, timeStr)

        loadMapInWebView(location.latitude, location.longitude)
    }

    private fun loadMapInWebView(latitude: Double, longitude: Double) {
        try {
            val htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        html, body {
                            margin: 0;
                            padding: 0;
                            width: 100%;
                            height: 100%;
                            overflow: hidden;
                            background-color: #f5f5f5;
                        }
                        iframe {
                            width: 100%;
                            height: 100%;
                            border: 0;
                        }
                    </style>
                </head>
                <body>
                    <iframe
                        src="https://maps.google.com/maps?q=$latitude,$longitude&z=15&output=embed"
                        allowfullscreen>
                    </iframe>
                </body>
                </html>
            """.trimIndent()

            mapWebView.loadDataWithBaseURL(
                "https://maps.google.com",
                htmlContent,
                "text/html",
                "UTF-8",
                null,
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnGetLocation.isEnabled = !isLoading
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lastLatitude?.let { outState.putDouble(KEY_LAT, it) }
        lastLongitude?.let { outState.putDouble(KEY_LNG, it) }
    }

    override fun onPause() {
        super.onPause()
        if (isReceivingUpdates) {
            stopLocationUpdates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        try {
            mapWebView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val KEY_LAT = "key_latitude"
        private const val KEY_LNG = "key_longitude"
    }
}
