package com.arakadds.arak.presentation.activities.map

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.arakadds.arak.R
import com.arakadds.arak.common.helper.LocaleHelper
import com.arakadds.arak.common.helper.connectivity_checker.ConnectionLiveData
import com.arakadds.arak.common.preferaence.IPreferenceHelper
import com.arakadds.arak.common.preferaence.PreferenceManager
import com.arakadds.arak.databinding.ActivityGoogleMapBinding
import com.arakadds.arak.presentation.activities.other.BaseActivity
import com.arakadds.arak.presentation.dialogs.ApplicationDialogs
import com.arakadds.arak.presentation.viewmodel.ArakServicesViewModel
import com.arakadds.arak.utils.AppProperties
import com.arakadds.arak.utils.Constants.DEFAULT_ZOOM
import com.arakadds.arak.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.arakadds.arak.utils.GlobalMethodsOldClass
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import dagger.android.AndroidInjection
import io.paperdb.Paper
import javax.inject.Inject

class MapActivity : BaseActivity(), OnMapReadyCallback, ApplicationDialogs.AlertDialogCallbacks {
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        if (locationPermissionGranted) {
            getDeviceLocation()
            googleMap.setOnMapClickListener { latLng -> //initial marker option
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
                //remove all marker
                googleMap.clear()
                //animation to move the camera
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng, DEFAULT_ZOOM
                    )
                )
                //add marker on map
                googleMap.addMarker(markerOptions)
                lat = latLng.latitude.toString()
                lon = latLng.longitude.toString()
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            getLocationPermission()
            return
        }
        gMap!!.isMyLocationEnabled = true
    }

    private val preferenceHelper: IPreferenceHelper by lazy { PreferenceManager(applicationContext) }
    lateinit var binding: ActivityGoogleMapBinding
    private lateinit var activityContext: Context

    private lateinit var toolbar: Toolbar
    private lateinit var pageTitle: TextView
    private var connectionLiveData: ConnectionLiveData? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ArakServicesViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var token: String
    private val TAG = "MapActivity"
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private var locationPermissionGranted = false
    private var gMap: GoogleMap? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var GPSSensor: String? = null
    private var lat: String? = null
    private var lon: String? = null
    private lateinit var resources: Resources
    private lateinit var language: String
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectionLiveData = ConnectionLiveData(this)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AndroidInjection.inject(this)
        activityContext = this

        language = preferenceHelper.getLanguage()
        updateMapView(language)
        iniUi()
        initToolbar()
        getLocationPermission()
        setListeners()
    }

    private fun initToolbar() {
        toolbar = binding.appBarLayout.findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        pageTitle = binding.appBarLayout.findViewById(R.id.page_title)
        val backImageView: ImageView = binding.appBarLayout.findViewById(R.id.back_button)
        //shareImageView = binding.appBarLayout.findViewById(R.id.share_button)
        //favoriteImageView = binding.appBarLayout.findViewById(R.id.favorite_button)

        pageTitle.text = resources.getString(R.string.Create_New_Ad_activity_Location)
        backImageView.setOnClickListener { finish() }
    }

    private fun updateMapView(language: String) {
        val context = LocaleHelper.setLocale(this@MapActivity, language)
        resources = context.resources
        binding.confirmLocationButtonId.text = resources.getString(R.string.map_activity_confirm)
        this.language = language
    }
    private fun iniUi() {
        GPSSensor = getResources().getString(R.string.Your_GPS)
    }
    fun setListeners() {
        binding.confirmLocationButtonId.setOnClickListener { v: View? ->
            if (lat != null && lon != null) {

                preferenceHelper.setLat(lat!!)
                preferenceHelper.setLon(lon!!)

                val latLng = LatLng(lat!!.toDouble(), lon!!.toDouble())
                val selectedLocationName =
                    GlobalMethodsOldClass.getCoordinatesAddressName(this@MapActivity, latLng)
                preferenceHelper.setSelectedLocationName(selectedLocationName)
                finish()
            } else {
                Toast.makeText(activityContext,
                    resources.getString(R.string.toast_please_set_location_map), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this@MapActivity)
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(GPSSensor)
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                /*Intent i = new Intent(HomeMapActivity.this,HomeMapActivity.class);
                        startActivity(i);*/
            }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun getDeviceCurrentLocation() {}

    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
                initMap()
            } else {
                ActivityCompat.requestPermissions(
                    this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this, permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun moveCamera(latLng: LatLng, zoom: Float, title: String?) {
        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    private fun getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (locationPermissionGranted) {
                val location: Task<Location> = mFusedLocationProviderClient.lastLocation
                location.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentLocation = task.result
                        if (currentLocation != null) {
                            moveCamera(
                                LatLng(
                                    currentLocation.latitude,
                                    currentLocation.longitude
                                ), DEFAULT_ZOOM, "current location"
                            )
                        } else {
                            statusCheck()
                        }
                    } else {
                        Toast.makeText(
                            activityContext,
                            resources.getString(R.string.toast_unable_current_location),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    fun statusCheck() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                var i = 0
                while (i < grantResults.size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        locationPermissionGranted = false
                        return
                    }
                    i++
                }
                locationPermissionGranted = true
                initMap()
            }
        }
    }*/


    override fun onClose() {
    }

    override fun onConfirm(actionType: Int) {

    }
}