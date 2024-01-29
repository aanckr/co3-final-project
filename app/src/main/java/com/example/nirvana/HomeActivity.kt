package com.example.nirvana

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.amadeus.Amadeus
import com.amadeus.Params
import com.example.nirvana.databinding.ActivityHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import java.util.*
import kotlin.math.sqrt


class HomeActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchView: SearchView
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_home)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        //searchView = findViewById(R.id.searchView)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_collections -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Collections", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_contact_us -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Contact Us", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_log_out -> {
                    // for now:
                    Toast.makeText(applicationContext, "Clicked Log Out", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        val myLocation = findViewById<ImageView>(R.id.my_location)
        myLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)

            } else {
                getLocation()
            }
        }
    }

    private fun getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Geocoder initialisieren
                        val geocoder = Geocoder(this, Locale.getDefault())

                        try {
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val cityName = addresses[0].locality
                                searchView.setQuery(latitude.toString(), false)
                                Toast.makeText(this, "Stadt: $cityName, Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_LONG).show()
                                getActivities(latitude, longitude)
                            } else {
                                Toast.makeText(this, "Adresse nicht gefunden", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: IOException) {
                            Toast.makeText(this, "Geocoder-Dienst nicht verfügbar", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Standort nicht verfügbar", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Fehler beim Abrufen des Standorts", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun getActivities(latitude: Double, longitude: Double) {
        val amadeus = Amadeus.builder("HDFV1svGcpeg0kMymEmPVGkU4JNf5h7v", "dWVYpb8M8wIm1hzc").build()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = withContext(Dispatchers.IO) {
                    amadeus.referenceData.locations.pointsOfInterest.get(
                        Params.with("latitude", latitude)
                            .and("longitude", longitude)
                            .and("radius", 20)
                    )
                }
                Toast.makeText(this@HomeActivity, response[0].name, Toast.LENGTH_LONG).show()
                saveActivitiesInDB(response, latitude, longitude)
            }catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Fehler beim Abrufen der Aktivitäten", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveActivitiesInDB(response: Array<com.amadeus.resources.PointOfInterest>, latitude: Double, longitude: Double) {
        Toast.makeText(this, "size: ${response.size}", Toast.LENGTH_LONG).show()
        for (activity in response) {
            val name = activity.name
            val category = activity.category
            val rank = Random().nextInt(5) + 1
            val tags = activity.tags.asList()
            val activityLatitude = activity.geoCode.latitude
            val activityLongitude = activity.geoCode.longitude
            val id = UUID.randomUUID().toString()
            val distance = sqrt((latitude - activityLatitude) * (latitude - activityLatitude) + (longitude - activityLongitude) * (longitude - activityLongitude))

            database = FirebaseDatabase.getInstance().getReference("Activities")
            val activity = Activities(name, category, rank, tags, activityLatitude, activityLongitude, id, distance)
            database.child(name).setValue(activity).addOnSuccessListener {
                Toast.makeText(this, "Activity added", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if (toggle.onOptionsItemSelected(item)) {
           return true
       }
        return super.onOptionsItemSelected(item)
    }
}